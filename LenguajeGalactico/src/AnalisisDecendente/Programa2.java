package AnalisisDecendente;

import java.io.*;
import java.util.*;

public class Programa2 {

    static Map<String, Map<String, String>> tablaPredictiva = new HashMap<>();
    static List<String> derivacion = new ArrayList<>();

    static {
        // Definimos la tabla predictiva con números de regla (sin "λ" como texto)
        String[][] tabla = {
                // +      *       (      )      id      $
                {" ",   " ",    "1",   " ",    "1",    " "},   // E
                {"2",   " ",    " ",   "3",    " ",    "3"},   // E'
                {" ",   " ",    "4",   " ",    "4",    " "},   // T
                {"6",   "5",    " ",   "6",    " ",    "6"},   // T'
                {" ",   " ",    "7",   " ",    "8",    " "}    // F
        };

        String[] filas = {"E", "E'", "T", "T'", "F"};
        String[] columnas = {"+", "*", "(", ")", "id", "$"};

        for (int i = 0; i < filas.length; i++) {
            tablaPredictiva.put(filas[i], new HashMap<>());
            for (int j = 0; j < columnas.length; j++) {
                if (!tabla[i][j].isEmpty())
                    tablaPredictiva.get(filas[i]).put(columnas[j], tabla[i][j]);
            }
        }
    }

    public static List<String> leerArchivo(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea = br.readLine();
        br.close();

        List<String> tokens = new ArrayList<>();
        if (linea != null && !linea.trim().isEmpty()) {
            tokens.addAll(Arrays.asList(linea.trim().split("\\s+")));
        }
        tokens.add("$"); // Añadir el símbolo de fin como token separado
        return tokens;
    }

    public static void analizar(List<String> entrada) {
        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push("E"); // símbolo inicial

        System.out.printf("| %-30s | %-40s |\n", "PILA", "ENTRADA");

        while (!pila.isEmpty()) {
            String pilaTop = pila.peek();
            String entradaTop = entrada.get(0);

            System.out.printf("| %-30s | %-40s |\n", pila, String.join(" ", entrada));

            if (pilaTop.equals(entradaTop)) {
                pila.pop();
                entrada.remove(0);
            } else if (esTerminal(pilaTop)) {
                System.out.println("✖ Error: Terminal no coincide: " + pilaTop + " vs " + entradaTop);
                return;
            } else {
                String regla = tablaPredictiva.getOrDefault(pilaTop, new HashMap<>()).getOrDefault(entradaTop, null);
                if (regla == null) {
                    System.out.println("✖ Error sintáctico: No hay regla para [" + pilaTop + ", " + entradaTop + "]");
                    return;
                }

                pila.pop();

                switch (regla) {
                    case "1": // E → T E'
                        derivacion.add("E → T E'");
                        pila.push("E'");
                        pila.push("T");
                        break;
                    case "2": // E' → + T E'
                        derivacion.add("E' → + T E'");
                        pila.push("E'");
                        pila.push("T");
                        pila.push("+");
                        break;
                    case "3": // E' → λ
                        derivacion.add("E' → λ");
                        break;
                    case "4": // T → F T'
                        derivacion.add("T → F T'");
                        pila.push("T'");
                        pila.push("F");
                        break;
                    case "5": // T' → * F T'
                        derivacion.add("T' → * F T'");
                        pila.push("T'");
                        pila.push("F");
                        pila.push("*");
                        break;
                    case "6": // T' → λ
                        derivacion.add("T' → λ");
                        break;
                    case "7": // F → ( E )
                        derivacion.add("F → ( E )");
                        pila.push(")");
                        pila.push("E");
                        pila.push("(");
                        break;
                    case "8": // F → id
                        derivacion.add("F → id");
                        pila.push("id");
                        break;
                    default:
                        System.out.println("✖ Regla no reconocida: " + regla);
                        return;
                }
            }
        }

        if (entrada.size() == 0) {
            System.out.println("✔ Palabra aceptada.");
            System.out.println("\n📘 Árbol de derivación:");
            for (String regla : derivacion) {
                System.out.println("  " + regla);
            }
        } else {
            System.out.println("✖ Entrada no consumida completamente.");
        }
    }

    static boolean esTerminal(String simbolo) {
        return !simbolo.matches("[A-Z]+'?");
    }

    public static void main(String[] args) {
        try {
            List<String> entrada = leerArchivo("C:\\Users\\Ruy\\Downloads\\Escuela\\6to Semestre\\Compiladores\\Entrada2.txt");
            analizar(entrada);
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }
}


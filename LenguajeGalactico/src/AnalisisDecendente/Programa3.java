package AnalisisDecendente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Programa3 {

    static Map<String, Map<String, String>> tablaPredictiva = new HashMap<>();
    static List<String> derivacion = new ArrayList<>();

    static {
        // tabla predictiva
        String[][] tabla = {
                // ant    all     big     bad     bus     boss     cat     $
                {"2",     "2",    "1",    " ",    "1",    "1",    "1",    " "},
                {" ",     " ",    "3",    " ",    "4",    " ",    " ",    " "},
                {" ",     "7",    " ",    "",     " ",    " ",    "6",    " "}
        };

        String[] filas = {"A", "B", "C"};
        String[] columnas = {"ant", "all", "big", "bad", "bus", "boss", "cat", "$"};

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
        tokens.add("$"); // Añadir símbolo de fin como token separado
        return tokens;
    }

    public static void analizar(List<String> entrada) {
        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push("A");

        System.out.printf("| %-20s | %-40s |\n", "PILA", "ENTRADA");

        while (!pila.isEmpty()) {
            String pilaTop = pila.peek();
            String entradaTop = entrada.get(0);

            System.out.printf("| %-20s | %-40s |\n", pila, String.join(" ", entrada));

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
                    case "1": // A -> BC
                        derivacion.add("A → B C");
                        pila.push("C");
                        pila.push("B");
                        break;
                    case "2": // A -> ant A all
                        derivacion.add("A → ant A all");
                        pila.push("all");
                        pila.push("A");
                        pila.push("ant");
                        break;
                    case "3": // B -> big C bad
                        derivacion.add("B → big C bad");
                        pila.push("bad");
                        pila.push("C");
                        pila.push("big");
                        break;
                    case "4": // B -> bus A boss
                        derivacion.add("B → bus A boss");
                        pila.push("boss");
                        pila.push("A");
                        pila.push("bus");
                        break;
                    case "5": // B -> λ
                        derivacion.add("B → λ");
                        break;
                    case "6": // C -> cat
                        derivacion.add("C → cat");
                        pila.push("cat");
                        break;
                    case "7": // C -> λ 
                        derivacion.add("C → λ");
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
        return !simbolo.matches("[A-Z]");
    }

    public static void main(String[] args) {
        try {
            List<String> entrada = leerArchivo("C:\\Users\\Ruy\\Downloads\\Escuela\\6to Semestre\\Compiladores\\Entrada3.txt");
            analizar(entrada);
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }
}






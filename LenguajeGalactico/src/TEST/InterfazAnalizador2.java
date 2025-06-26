package TEST;

import javax.swing.*;

import AnalisisSintaxico.*;
import AnalisisDecendente.*;
import AnalisisAcendente.*;

import java.awt.*;
import java.io.*;
import java.util.List;

public class InterfazAnalizador2 extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea consola;
    private JComboBox<String> tipoAnalisis;
    private JComboBox<String> seleccionCompiladorLexico;
    private JComboBox<String> seleccionCompiladorDescendente;
    private JComboBox<String> seleccionCompiladorAscendente;

    private JLabel labelCompiladorLexico;
    private JLabel labelCompiladorDescendente;
    private JLabel labelCompiladorAscendente;

    public InterfazAnalizador2() {
    	//especifiaciones del diseño de la interfaz principal
        setTitle("Analizador SGA");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        areaCodigo = new JTextArea();
        consola = new JTextArea();
        consola.setEditable(false);
        consola.setBackground(Color.BLACK);
        consola.setForeground(Color.YELLOW);
        consola.setFont(new Font("Monospaced", Font.PLAIN, 14));

        tipoAnalisis = new JComboBox<>(new String[]{
                "Léxico", "Sintáctico Descendente", "Sintáctico Ascendente"
        });
        seleccionCompiladorLexico = new JComboBox<>(new String[]{
                "Compilador1", "Compilador2", "Compilador3", "Compilador4"
        });
        seleccionCompiladorDescendente = new JComboBox<>(new String[]{
                "Programa1", "Programa2", "Programa3", "Programa4", "Programa5"
        });
        seleccionCompiladorAscendente = new JComboBox<>(new String[]{
                "Programa6", "Programa7", "Programa8", "Programa9", "Programa10"
        });

        // Etiquetas
        labelCompiladorLexico = new JLabel("Compilador Léxico:");
        labelCompiladorDescendente = new JLabel("Compilador Descendente:");
        labelCompiladorAscendente = new JLabel("Compilador Ascendente:");

        // Menú
        JMenuBar barra = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem abrir = new JMenuItem("Abrir Proyecto");
        JMenuItem guardar = new JMenuItem("Guardar Proyecto");
        JMenuItem cerrar = new JMenuItem("Cerrar Proyecto");

        abrir.addActionListener(e -> abrirArchivo());
        guardar.addActionListener(e -> guardarArchivo());
        cerrar.addActionListener(e -> {
            areaCodigo.setText("");
            consola.setText("");
        });

        menuArchivo.add(abrir);
        menuArchivo.add(guardar);
        menuArchivo.add(cerrar);
        barra.add(menuArchivo);

        JMenu menuAnalizar = new JMenu("Ejecutar");
        JMenuItem ejecutar = new JMenuItem("Analizar Código");
        ejecutar.addActionListener(e -> analizarCodigo());
        menuAnalizar.add(ejecutar);
        barra.add(menuAnalizar);

        setJMenuBar(barra);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(new JScrollPane(areaCodigo), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setPreferredSize(new Dimension(850, 220));
        panelInferior.add(new JLabel("Consola de resultados:"), BorderLayout.NORTH);
        panelInferior.add(new JScrollPane(consola), BorderLayout.CENTER);

        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControl.add(new JLabel("Tipo de análisis:"));
        panelControl.add(tipoAnalisis);
        panelControl.add(labelCompiladorLexico);
        panelControl.add(seleccionCompiladorLexico);
        panelControl.add(labelCompiladorDescendente);
        panelControl.add(seleccionCompiladorDescendente);
        panelControl.add(labelCompiladorAscendente);
        panelControl.add(seleccionCompiladorAscendente);
        panelInferior.add(panelControl, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelSuperior, BorderLayout.CENTER);
        getContentPane().add(panelInferior, BorderLayout.SOUTH);

        // Visibilidad inicial
        actualizarVisibilidadComponentes();

        tipoAnalisis.addActionListener(e -> {
            consola.setText("");
            actualizarVisibilidadComponentes();
        });
    }

    private void actualizarVisibilidadComponentes() {
        String tipo = (String) tipoAnalisis.getSelectedItem();

        labelCompiladorLexico.setVisible("Léxico".equals(tipo));
        seleccionCompiladorLexico.setVisible("Léxico".equals(tipo));

        labelCompiladorDescendente.setVisible("Sintáctico Descendente".equals(tipo));
        seleccionCompiladorDescendente.setVisible("Sintáctico Descendente".equals(tipo));

        labelCompiladorAscendente.setVisible("Sintáctico Ascendente".equals(tipo));
        seleccionCompiladorAscendente.setVisible("Sintáctico Ascendente".equals(tipo));
    }
    //metodos a usar para guardar, abrir, abalizar codigo
    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                areaCodigo.setText("");
                String linea;
                while ((linea = br.readLine()) != null) {
                    areaCodigo.append(linea + "\n");
                }
            } catch (IOException ex) {
                mostrarError("Error al leer archivo: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(chooser.getSelectedFile()))) {
                bw.write(areaCodigo.getText());
            } catch (IOException ex) {
                mostrarError("Error al guardar archivo: " + ex.getMessage());
            }
        }
    }

    private void analizarCodigo() {
        consola.setText("");
        String codigo = areaCodigo.getText();
        String tipo = (String) tipoAnalisis.getSelectedItem();

        PrintStream consolaStream = new PrintStream(new OutputStream() {
            public void write(int b) {
                consola.append(String.valueOf((char) b));
            }
        });

        //funciones para la ventana desplegable para legir el tipo de compilador
        PrintStream originalOut = System.out;
        System.setOut(consolaStream);

        try {
            switch (tipo) {
                case "Léxico" -> ejecutarAnalisisLexico(codigo, (String) seleccionCompiladorLexico.getSelectedItem());
                case "Sintáctico Descendente" -> ejecutarAnalisisDescendente(codigo, (String) seleccionCompiladorDescendente.getSelectedItem());
                case "Sintáctico Ascendente" -> ejecutarAnalisisAscendente((String) seleccionCompiladorAscendente.getSelectedItem());
            }
        } catch (Exception e) {
            mostrarError("Error al analizar: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

    //Metodo utilizado para leer el codigo del tipo de compilador seleccionado de los codigos importados del package "AnalisisLexico"
    private void ejecutarAnalisisLexico(String codigo, String compilador) {
        consola.append("=== Análisis léxico con " + compilador + " ===\n");
        switch (compilador) {
            case "Compilador1" -> analizarConCompilador(codigo, Compilador1::transformar, Compilador1::evaluar, 4); //Binario
            case "Compilador2" -> analizarConCompilador(codigo, Compilador2::transformar, Compilador2::evaluar, 7); //Ecuacion cientifica
            case "Compilador3" -> analizarConCompilador(codigo, Compilador3::transformar, Compilador3::evaluar, 13);//Ecuacion cuadratica
            case "Compilador4" -> analizarConCompilador(codigo, Compilador4::transformar, Compilador4::evaluar, 48); //CURP
            default -> consola.append("❌ Compilador léxico no válido.\n");
        }
    }
    //Metodo utilizado para leer codigo del tipo compilador seleccionado de los codigos importados del package "AnalisisAscendente"
    private void ejecutarAnalisisDescendente(String codigo, String programa) throws IOException {
        File temp = File.createTempFile("descendente_input", ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(codigo);
        }

        List<String> entrada = switch (programa) {
            case "Programa1" -> Programa1.leerArchivo(temp.getAbsolutePath());//cow
            case "Programa2" -> Programa2.leerArchivo(temp.getAbsolutePath());//F
            case "Programa3" -> Programa3.leerArchivo(temp.getAbsolutePath());//bad
            case "Programa4" -> Programa4.leerArchivo(temp.getAbsolutePath());//T
            case "Programa5" -> Programa5.leerArchivo(temp.getAbsolutePath());//PyC
            default -> null;
        };

        if (entrada != null) {
            switch (programa) {
                case "Programa1" -> Programa1.analizar(entrada);
                case "Programa2" -> Programa2.analizar(entrada);
                case "Programa3" -> Programa3.analizar(entrada);
                case "Programa4" -> Programa4.analizar(entrada);
                case "Programa5" -> Programa5.analizar(entrada);
            }
        } else {
            consola.append("❌ Programa descendente no válido.\n");
        }

        temp.delete();
    }
    //Metodo utilizado para leer el codigo del tipo de compilador seleccionado de los codigos importados del package "AnalisisDecendente"
    private void ejecutarAnalisisAscendente(String programa) throws Exception {
        String codigo = areaCodigo.getText();
        if (codigo == null || codigo.trim().isEmpty()) {
            consola.append("❌ El área de código está vacía, no se puede analizar.\n");
            return;
        }

        switch (programa) {
            case "Programa6" -> Programa6.analizar(codigo);  //F
            case "Programa7" -> Programa7.analizar(codigo);  //PyC
            case "Programa8" -> Programa8.analizar(codigo);  //cow
            case "Programa9" -> Programa9.analizar(codigo);  //bad
            case "Programa10" -> Programa10.analizar(codigo);//T
            default -> consola.append("❌ Programa ascendente no válido.\n");
        }
    }

    
    //funcion para leer y analizar cada codigo ombtenido de la importacion 

    private void analizarConCompilador(String codigo, Transformar transformar, Evaluar evaluar, int estadoFinal) {
        for (String linea : codigo.split("\n")) {
            linea = linea.split("//")[0].trim();
            consola.append("\n➡ Línea: " + linea + "\n");

            int estado = 0;
            boolean esValida = true;

            for (char c : linea.toCharArray()) {
                int entrada = transformar.aplicar(c);
                if (entrada == -1) {
                    consola.append("❌ Caracter no válido: '" + c + "'\n");
                    esValida = false;
                    break;
                }
                int estadoAnterior = estado;
                estado = evaluar.aplicar(estado, entrada);
                if (estado == -1) {
                    consola.append("❌ Transición inválida: estado " + estadoAnterior + " con '" + c + "'\n");
                    esValida = false;
                    break;
                }
                consola.append("✔ Transición: " + estadoAnterior + " → " + estado + " con '" + c + "'\n");
            }

            consola.append(esValida && estado == estadoFinal ? "✅ Resultado: Correcto\n" : "❌ Resultado: No correcto\n");
        }
    }
  //interfaces a utiliar para la lectura de los codigo
    
    @FunctionalInterface interface Transformar { int aplicar(char c); }
    @FunctionalInterface interface Evaluar { int aplicar(int estado, int entrada); }
    
    //mostrar mensajes en caso de error

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
//metodo para inicializar la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazAnalizador2().setVisible(true));
    }
}

package org.example;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.print("""
                1 - TEST 1
                2 - TEST 2
                3 - TEST 3
                E - ERROR TEST
                """);
        System.out.print("Input: ");
        FileInputStream inputFile = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input_line = reader.readLine();

        switch (input_line) {
            case "1" -> {
                try {
                    inputFile = new FileInputStream("test1.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
            case "2" -> {
                try {
                    inputFile = new FileInputStream("test2.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
            case "3" -> {
                try {
                    inputFile = new FileInputStream("test3.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
            case "E" -> {
                try {
                    inputFile = new FileInputStream("errors.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        assert inputFile != null;
        CharStream input = new UnbufferedCharStream(inputFile, 2048);
        ExprLexer lexer = new ExprLexer(input);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);

        parser.addErrorListener(new VerboseListener());
        ParseTree tree = parser.prog();

        if (parser.getNumberOfSyntaxErrors() == 0) {
            EvalVisitor evalVisitor = new EvalVisitor();
            var value = evalVisitor.visit(tree);
            if (value.type != Type.Error)
            {
                if (Errors.getNumberOfErrors() != 0)
                {
                    Errors.printAndClearErrors();
                    return;
                }
                StackVisitor stackVisitor = new StackVisitor();
                stackVisitor.visit(tree);
                stackVisitor.printStackToFile("stack_output.txt");
                //stackVisitor.printStack();

                VirtualMachine VM = new VirtualMachine();
                VM.giveInstructions(stackVisitor.getStack());
                VM.run();
            }
            else {
                System.out.print("ERROR");
            }
        }
    }
}
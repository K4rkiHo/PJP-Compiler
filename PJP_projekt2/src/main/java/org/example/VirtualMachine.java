package org.example;

import java.io.*;
import java.util.*;

public class VirtualMachine {
    private List<StackVisitor.Entry> instructions = new ArrayList<>();
    private final Stack<Object> stack = new Stack<>();
    private final HashMap<String, Object> variables = new HashMap<>();
    private final HashMap<Integer, Integer> labels = new HashMap<>();
    public void giveInstructions(List<StackVisitor.Entry> instructions) {
        this.instructions = instructions;
    }
    private void scanForLabels() {
        for (int i = 0; i < instructions.size(); i++) {
            var entry = instructions.get(i);

            if(entry.ins.equals("label")) {
                labels.put((Integer) entry.value1, i);
            }
        }
    }

    public void run() throws IOException {

        //Predem si to nacita na navesti, aby vedela, kde ma skocit
        scanForLabels();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        for (int i = 0; i < instructions.size(); i++) {
            var entry = instructions.get(i);

            Object v1, v2;

            switch (entry.ins) {
                case "add":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if (v1 instanceof Integer)
                        stack.push((int) v1 + (int) v2);
                    else
                        stack.push((float) v1 + (float) v2);
                    break;
                case "sub":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if (v1 instanceof Integer)
                        stack.push((int) v1 - (int) v2);
                    else
                        stack.push((float) v1 - (float) v2);
                    break;
                case "mul":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if (v1 instanceof Float && v2 instanceof Float) {
                        Float float1 = (Float) v1;
                        Float float2 = (Float) v2;
                        Float result = float1 * float2;
                        stack.push(result);
                    }
                    if (v1 instanceof Integer && v2 instanceof Integer) {
                        Integer int_1 = (Integer) v1;
                        Integer int_2 = (Integer) v2;
                        Integer result = int_1 * int_2;
                        stack.push(result);
                    }
                    if (v1 instanceof Float && v2 instanceof Integer)
                    {
                        Float float_1 = (Float) v1;
                        Integer int_2 = (Integer) v2;
                        Float result = float_1 * int_2;
                        stack.push(result);
                    }
                    if (v1 instanceof Integer && v2 instanceof Float)
                    {
                        Integer int_1 = (Integer) v1;
                        Float float_2 = (Float) v2;
                        Float result = int_1 * float_2;
                        stack.push(result);
                    }
                    break;
                case "div":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if(v1 instanceof Integer)
                        stack.push((int)v2 / (int)v1);
                    else
                        stack.push((float)v2 / (float)v1);
                    break;
                case "mod":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    stack.push((int)v2 % (int)v1);
                    break;
                case "uminus":
                    v1 = stack.pop();
                    if(v1 instanceof Integer)
                        stack.push(-((int)v1));
                    else
                        stack.push(-((float)v1));
                    break;
                case "concat":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    stack.push((String)v2 + v1);
                    break;
                case "and":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    stack.push((boolean)v1 && (boolean)v2);
                    break;
                case "or":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    stack.push((boolean)v1 || (boolean)v2);
                    break;
                case "gt":
                    /*
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if(v1 instanceof Integer)
                        stack.push((int)v1 < (int)v2);
                    else
                        stack.push((float)v1 < (float)v2);

                     */

                    v1 = stack.pop();
                    v2 = stack.pop();
                    if (v1 instanceof Float && v2 instanceof Float) {
                        Float float1 = (Float) v1;
                        Float float2 = (Float) v2;
                        Boolean result = float1 < float2;
                        stack.push(result);
                    }
                    if (v1 instanceof Integer && v2 instanceof Integer) {
                        Integer int_1 = (Integer) v1;
                        Integer int_2 = (Integer) v2;
                        Boolean result = int_1 < int_2;
                        stack.push(result);
                    }
                    if (v1 instanceof Float && v2 instanceof Integer)
                    {
                        Float float_1 = (Float) v1;
                        Integer int_2 = (Integer) v2;
                        Boolean result = float_1 < int_2;
                        stack.push(result);
                    }
                    if (v1 instanceof Integer && v2 instanceof Float)
                    {
                        Integer int_1 = (Integer) v1;
                        Float float_2 = (Float) v2;
                        Boolean result = int_1 < float_2;
                        stack.push(result);
                    }

                    break;
                case "lt":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if(v1 instanceof Integer)
                        stack.push((int)v1 > (int)v2);
                    else
                        stack.push((float)v1 > (float)v2);
                    break;
                case "eq":
                    v1 = stack.pop();
                    v2 = stack.pop();
                    if(v1 instanceof Integer)
                        stack.push((int)v1 == (int)v2);
                    else if(v1 instanceof Float)
                        stack.push((float)v1 == (float)v2);
                    else
                        stack.push(((String)v1).equals(v2));
                    break;
                case "not":
                    v1 = stack.pop();
                    stack.push(!(boolean)v1);
                    break;
                    //int to float
                case "itof":
                    v1 = stack.pop();
                    stack.push(((Integer)v1).floatValue());
                    break;
                case "push":
                    stack.push(entry.value2);
                    break;
                case "pop":
                    stack.pop();
                    break;
                case "load":
                    stack.push(variables.get((String)entry.value1));
                    break;
                case "save":
                    v1 = stack.pop();
                    variables.put((String)entry.value1, v1);
                    break;
                case "label":
                    labels.put((Integer) entry.value1, i);
                    break;
                case "jmp":
                    i = labels.get((Integer) entry.value1);
                    break;
                case "fjmp":
                    v1 = stack.pop();
                    if(!(boolean) v1)
                        i = labels.get((Integer) entry.value1);
                    break;
                case "print":
                    Stack<Object> stack1 = new Stack<>();
                    for (int k = 0; k < (Integer) entry.value1; k++) {
                        v1 = stack.pop();
                        stack1.push(v1);
                    }

                    Collections.reverse(stack1);

                    for (var e : stack1) {
                        if(e instanceof String)
                            System.out.print(((String) e).replace("\"", ""));
                        else
                            System.out.print(e);
                    }

                    System.out.println();

                    break;
                case "read":
                    switch (((String) entry.value1)) {
                        case "I" -> stack.push(Integer.parseInt(br.readLine()));
                        case "F" -> stack.push(Float.parseFloat(br.readLine()));
                        case "B" -> stack.push(Boolean.parseBoolean(br.readLine()));
                        default -> stack.push(br.readLine());
                    }
                    break;
                default:
                    System.err.println("sum ting wong\ni: " + i);
            }
        }
    }
    public void loadInstructionsFromFile(String fileName) throws FileNotFoundException {
        File myObj = new File(fileName);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            var ins = data.split(" ");

            switch (ins[0]) {
                case "push" -> {
                    if(ins.length > 3) {
                        String tmp = "";
                        for (int i = 2; i < ins.length; i++) {
                            tmp = tmp.concat(ins[i]);
                        }
                        instructions.add(new StackVisitor.Entry("push", ins[1], tmp));
                    } else {
                        instructions.add(new StackVisitor.Entry("push", ins[1], ins[2]));
                    }
                }
                case "load", "save", "label", "jmp", "fjmp", "print", "read" -> instructions.add(new StackVisitor.Entry(ins[0], ins[1]));
                default -> instructions.add(new StackVisitor.Entry(ins[0]));
            }
        }
        myReader.close();
        for (StackVisitor.Entry e : instructions) {
            System.out.printf(e.ins + " " + "\n");
        }
    }
}

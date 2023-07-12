package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StackVisitor extends ExprBaseVisitor<SymbolTable.Entry> {
    private final List<Entry> stack = new ArrayList<>();
    private final SymbolTable symbolTable = new SymbolTable();
    private int label = 0;
    public void printStack() {
        for (var entry : stack) {
            System.out.print(entry.ins);
            if (entry.value1 != null) {
                System.out.print(" " + entry.value1);
            }
            if (entry.value2 != null) {
                System.out.print(" " + entry.value2);
            }
            System.out.println();
        }
    }

    public List<Entry> getStack() {
        return stack;
    }

    public void printStackToFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("");
        for (var entry : stack) {
            String str = entry.ins;
            if (entry.value1 != null) {
                str = str.concat(" " + entry.value1);
            }
            if (entry.value2 != null) {
                str = str.concat(" " + entry.value2);
            }
            writer.append(str).append("\n");
        }
        writer.close();
    }

    private int getLabel() {
        return label++;
    }

    @Override
    public SymbolTable.Entry visitProg(ExprParser.ProgContext ctx) {
        for (var statement : ctx.stm()) {
            visit(statement);
        }
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitDeclaration(ExprParser.DeclarationContext ctx) {
        var type = visit(ctx.type());

        for (var identifier : ctx.IDENTIFIER()) {
            symbolTable.add(identifier.getSymbol(), type.type);
            if(type.type == Type.Int) {
                stack.add(new Entry("push", "I", 0));
            } else if(type.type == Type.Float) {
                stack.add(new Entry("push", "F", (float)0.0));
            } else if(type.type == Type.Boolean) {
                stack.add(new Entry("push", "B", true));
            } else {
                stack.add(new Entry("push", "S", "\"\""));
            }
            stack.add(new Entry("save", identifier.getText()));
        }
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitPrint(ExprParser.PrintContext ctx) {
        visit(ctx.expr());
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitType(ExprParser.TypeContext ctx) {
        if (ctx.type_v.getText().equals("int")) return new SymbolTable.Entry(Type.Int);
        else if (ctx.type_v.getText().equals("float")) return new SymbolTable.Entry(Type.Float);
        else if (ctx.type_v.getText().equals("bool")) return new SymbolTable.Entry(Type.Boolean);
        else return new SymbolTable.Entry(Type.String);
    }

    @Override
    public SymbolTable.Entry visitFloat(ExprParser.FloatContext ctx) {
        stack.add(new Entry("push", "F", Float.parseFloat(ctx.FLOAT().getText())));
        return new SymbolTable.Entry(Type.Float);
    }

    @Override
    public SymbolTable.Entry visitInteger(ExprParser.IntegerContext ctx) {
        stack.add(new Entry("push", "I", Integer.parseInt(ctx.INT().getText())));
        return new SymbolTable.Entry(Type.Int);
    }

    @Override
    public SymbolTable.Entry visitString(ExprParser.StringContext ctx) {
        stack.add(new Entry("push", "S", ctx.STRING().getText()));
        return new SymbolTable.Entry(Type.String);
    }

    @Override
    public SymbolTable.Entry visitBool(ExprParser.BoolContext ctx) {
        var value = ctx.getText();
        if (value.equals("true"))
        {
            stack.add(new Entry("push", "B", true));
            return new SymbolTable.Entry(Type.Boolean);
        }
        else if (value.equals("false"))
        {
            stack.add(new Entry("push", "B", false));
            return new SymbolTable.Entry(Type.Boolean);
        }
        else
        {
            return new SymbolTable.Entry(Type.Error);
        }
    }

    @Override
    public SymbolTable.Entry visitPrint_var(ExprParser.Print_varContext ctx) {
        stack.add(new Entry("load", ctx.IDENTIFIER().getText()));
        return symbolTable.get(ctx.IDENTIFIER().getSymbol());
    }

    @Override
    public SymbolTable.Entry visitPara(ExprParser.ParaContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public SymbolTable.Entry visitAdd(ExprParser.AddContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Float || right.type == Type.Float) {
            if (ctx.op.getText().trim().equals("+")) {
                stack.add(new Entry("add"));
                return new SymbolTable.Entry(Type.Float);
            } else {
                stack.add(new Entry("sub"));
                return new SymbolTable.Entry(Type.Float);
            }
        } else {
            if (ctx.op.getText().trim().equals("+")) {
                stack.add(new Entry("add"));
                return new SymbolTable.Entry(Type.Int);
            } else {
                stack.add(new Entry("sub"));
                return new SymbolTable.Entry(Type.Int);
            }
        }
    }

    @Override
    public SymbolTable.Entry visitMul(ExprParser.MulContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Float || right.type == Type.Float) {
            if (ctx.op.getText().trim().equals("*")) {
                var tmp =  new SymbolTable.Entry(Type.Float);
                stack.add(new Entry("mul"));
                return tmp;
            } else {
                var tmp = new SymbolTable.Entry(Type.Float);
                stack.add(new Entry("div"));
                return tmp;
            }
        } else {
            if (ctx.op.getText().trim().equals("*")) {
                stack.add(new Entry("mul"));
                return new SymbolTable.Entry(Type.Int);
            } else {
                stack.add(new Entry("div"));
                return new SymbolTable.Entry(Type.Int);
            }
        }
    }
    Boolean x = true;
    String s = "";
    @Override
    public SymbolTable.Entry visitAssign(ExprParser.AssignContext ctx) {
        var right = visit(ctx.expr());
        var variable = symbolTable.get(ctx.IDENTIFIER().getSymbol());

        if (x)
        {
            x = false;
            s = ctx.expr().getText();
        }

        if (variable.type == Type.Float && right.type == Type.Int) {
            var value = new SymbolTable.Entry(Type.Float);
            symbolTable.set(ctx.IDENTIFIER().getSymbol(), value);
            stack.add(new Entry("save", ctx.IDENTIFIER().getText()));
            stack.add(new Entry("load", stack.get(stack.size()-1).value1));

            if (Objects.equals(ctx.expr().getText(), s) && !x)
            {
                x = true;
                //stack.add(new Entry("pop"));
            }
            return value;
        }
        else {
            stack.add(new Entry("save", ctx.IDENTIFIER().getText()));
            stack.add(new Entry("load", stack.get(stack.size()-1).value1));

            symbolTable.set(ctx.IDENTIFIER().getSymbol(), right);

            if (Objects.equals(ctx.expr().getText(), s) && !x)
            {
                x = true;
                //stack.add(new Entry("pop"));
            }

            return right;
        }
    }

    @Override
    public SymbolTable.Entry visitRational(ExprParser.RationalContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if(left.type == Type.Float || right.type == Type.Float) {

            Entry tmp2 = null;

            if(left.type == Type.Int) {
                tmp2 = stack.remove(stack.size()-1);
            }

            if (ctx.op.getText().trim().equals("<")) {
                var tmp = new SymbolTable.Entry(Type.Boolean);
                if(tmp2 != null)
                    stack.add(tmp2);
                stack.add(new Entry("lt"));
                return tmp;
            } else {
                var tmp =  new SymbolTable.Entry(Type.Boolean);
                if(tmp2 != null)
                    stack.add(tmp2);
                stack.add(new Entry("gt"));
                return tmp;
            }
        } else {
            if (ctx.op.getText().trim().equals("<")) {
                var tmp = new SymbolTable.Entry(Type.Boolean);
                stack.add(new Entry("lt"));
                return tmp;
            } else {
                var tmp =  new SymbolTable.Entry(Type.Boolean);
                stack.add(new Entry("gt"));
                return tmp;
            }
        }
    }

    @Override
    public SymbolTable.Entry visitAnd(ExprParser.AndContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));
        stack.add(new Entry("and"));
        return new SymbolTable.Entry(Type.Boolean);
    }

    @Override
    public SymbolTable.Entry visitMod(ExprParser.ModContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));
        stack.add(new Entry("mod"));
        return new SymbolTable.Entry(Type.Int);
    }

    @Override
    public SymbolTable.Entry visitNot(ExprParser.NotContext ctx) {
        var expr = visit(ctx.expr());
        stack.add(new Entry("not"));
        return new SymbolTable.Entry(Type.Boolean);
    }

    @Override
    public SymbolTable.Entry visitOr(ExprParser.OrContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));
        stack.add(new Entry("or"));
        return new SymbolTable.Entry(Type.Boolean);
    }

    @Override
    public SymbolTable.Entry visitConcat(ExprParser.ConcatContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));
        stack.add(new Entry("concat"));
        return new SymbolTable.Entry(Type.String);
    }

    @Override
    public SymbolTable.Entry visitEquals(ExprParser.EqualsContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));
        stack.add(new Entry("eq"));
        if (ctx.op.getText().trim().equals("!=")) {
            stack.add(new Entry("not"));
        }
        if (left.type == Type.Float && right.type == Type.Float) {
            if (ctx.op.getText().trim().equals("=="))
                return new SymbolTable.Entry(Type.Boolean);
            else return new SymbolTable.Entry(Type.Boolean);
        }
        if (left.type == Type.Int && right.type == Type.Int) {
            if (ctx.op.getText().trim().equals("=="))
                return new SymbolTable.Entry(Type.Boolean);
            else return new SymbolTable.Entry(Type.Boolean);
        }
        if (left.type == Type.String && right.type == Type.String) {
            if (ctx.op.getText().trim().equals("=="))
                return new SymbolTable.Entry(Type.Boolean);
            else return new SymbolTable.Entry(Type.Boolean);
        }
        return new SymbolTable.Entry(Type.Error);
    }

    @Override
    public SymbolTable.Entry visitUminus(ExprParser.UminusContext ctx) {
        var expr = visit(ctx.expr());
        stack.add(new Entry("uminus"));
        if (expr.type == Type.Int) return new SymbolTable.Entry(Type.Int);
        else return new SymbolTable.Entry(Type.Float);
    }

    @Override
    public SymbolTable.Entry visitCode_stm(ExprParser.Code_stmContext ctx) {
        for (var statement : ctx.stm()) {
            visit(statement);
        }
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitRead(ExprParser.ReadContext ctx) {
        for (var id : ctx.IDENTIFIER()) {
            var type = symbolTable.get(id.getSymbol()).type;
            if(type == Type.Int) {
                stack.add(new Entry("read", "I"));
            } else if(type == Type.Float) {
                stack.add(new Entry("read", "F"));
            } else if(type == Type.Boolean) {
                stack.add(new Entry("read", "B"));
            } else if(type == Type.String) {
                stack.add(new Entry("read", "S"));
            }
            stack.add(new Entry("save", id.getText()));
        }
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitWrite(ExprParser.WriteContext ctx) {
        for (var expr : ctx.expr()) {
            visit(expr);
        }
        stack.add(new Entry("print", ctx.expr().size()));
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitIf(ExprParser.IfContext ctx) {
        visit(ctx.expr());
        var label = getLabel();
        var label2 = getLabel();
        stack.add(new Entry("fjmp", label));
        visit(ctx.stm(0));
        stack.add(new Entry("jmp", label2));
        stack.add(new Entry("label", label));
        if (ctx.stm().size() > 1) visit(ctx.stm(1));
        stack.add(new Entry("label", label2));
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitWhile(ExprParser.WhileContext ctx) {
        var label = getLabel();
        var label2 = getLabel();
        stack.add(new Entry("label", label));
        visit(ctx.expr());
        stack.add(new Entry("fjmp", label2));
        visit(ctx.stm());
        stack.add(new Entry("jmp", label));
        stack.add(new Entry("label", label2));
        return new SymbolTable.Entry(Type.OK);
    }

    @Override
    public SymbolTable.Entry visitEmpty(ExprParser.EmptyContext ctx) {
        return new SymbolTable.Entry(Type.OK);
    }

    public static class Entry {
        public String ins;
        public Object value1;
        public Object value2;
        public Entry(String ins, Object value1, Object value2) {
            this.ins = ins;
            this.value1 = value1;
            this.value2 = value2;
        }
        public Entry(String ins, Object value1) {
            this.ins = ins;
            this.value1 = value1;
        }
        public Entry(String ins) {
            this.ins = ins;
        }
    }
}

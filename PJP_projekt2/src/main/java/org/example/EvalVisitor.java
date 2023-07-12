package org.example;

import java.util.Objects;

public class EvalVisitor extends ExprBaseVisitor<SymbolTable.Entry>{
    private final SymbolTable symbolTable = new SymbolTable();
    @Override
    public SymbolTable.Entry visitProg(ExprParser.ProgContext ctx) {
        for (var statement : ctx.stm()) {
            visit(statement);
        }
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitInteger(ExprParser.IntegerContext ctx) {
        return new SymbolTable.Entry(Type.Int);
    }
    @Override
    public SymbolTable.Entry visitFloat(ExprParser.FloatContext ctx) {
        return new SymbolTable.Entry(Type.Float);
    }
    @Override
    public SymbolTable.Entry visitString(ExprParser.StringContext ctx) {
        return new SymbolTable.Entry(Type.String);
    }
    @Override
    public SymbolTable.Entry visitBool(ExprParser.BoolContext ctx) {
        var value = ctx.getText();
        if (value.equals("true"))
        {
            return new SymbolTable.Entry(Type.Boolean);
        }
        else if (value.equals("false"))
        {
            return new SymbolTable.Entry(Type.Boolean);
        }
        else
        {
            return new SymbolTable.Entry(Type.Error);
        }
    }
    @Override
    public SymbolTable.Entry visitPara(ExprParser.ParaContext ctx) {
        return visit(ctx.expr());
    }
    @Override
    public SymbolTable.Entry visitUminus(ExprParser.UminusContext ctx) {
        var expr = visit(ctx.expr());

        if (expr.type == Type.Error || expr.type == Type.Boolean || expr.type == Type.String) {
            Errors.reportError(ctx.getStart(), "Operator '-' can be only used with int and float, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (expr.type == Type.Int) return new SymbolTable.Entry(Type.Int);
        else return new SymbolTable.Entry(Type.Float);
    }
    @Override
    public SymbolTable.Entry visitNot(ExprParser.NotContext ctx) {
        var expr = visit(ctx.expr());

        if (expr.type != Type.Boolean) {
            Errors.reportError(ctx.getStart(), "Operator '!' can be only used with bool, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        return new SymbolTable.Entry(Type.Boolean);
    }
    private static float toFloat(Object value) {
        if (value instanceof Integer) return ((Integer) value).floatValue();
        else return (float) value;
    }
    @Override
    public SymbolTable.Entry visitMul(ExprParser.MulContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Error || right.type == Type.Error) return new SymbolTable.Entry(Type.Error);
        if (left.type == Type.Boolean || left.type == Type.String || right.type == Type.Boolean || right.type == Type.String) {
            Errors.reportError(ctx.op, "Operator '" + ctx.op.getText() + "' can be only used with int or float, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (left.type == Type.Float || right.type == Type.Float) {
            if (ctx.op.getText().trim().equals("*"))
                return new SymbolTable.Entry(Type.Float);
            else return new SymbolTable.Entry(Type.Float);
        } else {
            if (ctx.op.getText().trim().equals("*"))
                return new SymbolTable.Entry(Type.Int);
            else return new SymbolTable.Entry(Type.Int);
        }
    }
    @Override
    public SymbolTable.Entry visitMod(ExprParser.ModContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type != Type.Int || right.type != Type.Int) {
            Errors.reportError(ctx.getStart(), "Operator '%' can be only used with int, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        return new SymbolTable.Entry(Type.Int);
    }
    @Override
    public SymbolTable.Entry visitAdd(ExprParser.AddContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Error || right.type == Type.Error) return new SymbolTable.Entry(Type.Error);
        if (left.type == Type.Boolean || left.type == Type.String || right.type == Type.Boolean || right.type == Type.String) {
            Errors.reportError(ctx.op, "Operator '" + ctx.op.getText() + "' can be used only with int or float, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (left.type == Type.Float || right.type == Type.Float) {
            if (ctx.op.getText().trim().equals("+"))
                return new SymbolTable.Entry(Type.Float);
            else return new SymbolTable.Entry(Type.Float);
        } else {
            if (ctx.op.getText().trim().equals("+"))
                return new SymbolTable.Entry(Type.Int);
            else return new SymbolTable.Entry(Type.Int);
        }
    }
    @Override
    public SymbolTable.Entry visitConcat(ExprParser.ConcatContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type != Type.String || right.type != Type.String) {
            Errors.reportError(ctx.getStart(), "Operator '.' can be only used with string, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        return new SymbolTable.Entry(Type.String);
    }
    @Override
    public SymbolTable.Entry visitRational(ExprParser.RationalContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Error || right.type == Type.Error) return new SymbolTable.Entry(Type.Error);
        if (left.type == Type.Boolean || left.type == Type.String || right.type == Type.Boolean || right.type == Type.String) {
            Errors.reportError(ctx.op, "Operator '" + ctx.op.getText() + "' expects int or float, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (ctx.op.getText().trim().equals("<"))
            return new SymbolTable.Entry(Type.Boolean);
        else return new SymbolTable.Entry(Type.Boolean);
    }
    @Override
    public SymbolTable.Entry visitEquals(ExprParser.EqualsContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type == Type.Error || right.type == Type.Error || left.type == Type.Boolean || right.type == Type.Boolean) {
            Errors.reportError(ctx.op, "Operator '" + ctx.op.getText() + "' can't be used with bool.");
            return new SymbolTable.Entry(Type.Error);
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
    public SymbolTable.Entry visitAnd(ExprParser.AndContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type != Type.Boolean || right.type != Type.Boolean) {
            Errors.reportError(ctx.getStart(), "Operator '&&' can be only used with bool, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }
        return new SymbolTable.Entry(Type.Boolean);
    }
    @Override
    public SymbolTable.Entry visitOr(ExprParser.OrContext ctx) {
        var left = visit(ctx.expr(0));
        var right = visit(ctx.expr(1));

        if (left.type != Type.Boolean || right.type != Type.Boolean) {
            Errors.reportError(ctx.getStart(), "Operator '||' can be only used with bool, but something else was given.");
            return new SymbolTable.Entry(Type.Error);
        }
        return new SymbolTable.Entry(Type.Boolean);
    }
    //TODO projít
    @Override
    public SymbolTable.Entry visitAssign(ExprParser.AssignContext ctx) {
        var right = visit(ctx.expr());
        var variable = symbolTable.get(ctx.IDENTIFIER().getSymbol());

        if (variable.type == Type.Error || right.type == Type.Error) return new SymbolTable.Entry(Type.Error);

        if (variable.type == Type.Int && right.type == Type.Float) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is int, but the assigned value is float.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Int && right.type == Type.Boolean) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is int, but the assigned value is bool.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Int && right.type == Type.String) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is int, but the assigned value is string.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Float && right.type == Type.Boolean) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is float, but the assigned value is bool.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Float && right.type == Type.String) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is float, but the assigned value is string.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Boolean && right.type == Type.Int) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is bool, but the assigned value is int.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Boolean && right.type == Type.Float) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is bool, but the assigned value is float.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Boolean && right.type == Type.String) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is bool, but the assigned value is string.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.String && right.type == Type.Int) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is string, but the assigned value is int.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.String && right.type == Type.Float) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is string, but the assigned value is float.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.String && right.type == Type.Boolean) {
            Errors.reportError(ctx.IDENTIFIER().getSymbol(), "Variable '" + ctx.IDENTIFIER().getText() + "' type is string, but the assigned value is bool.");
            return new SymbolTable.Entry(Type.Error);
        }

        if (variable.type == Type.Float && right.type == Type.Int) {
            var value = new SymbolTable.Entry(Type.Float);
            symbolTable.set(ctx.IDENTIFIER().getSymbol(), value);
            return value;
        } else {
            symbolTable.set(ctx.IDENTIFIER().getSymbol(), right);
            return right;
        }
    }
    @Override
    public SymbolTable.Entry visitPrint_var(ExprParser.Print_varContext ctx) {
        return symbolTable.get(ctx.IDENTIFIER().getSymbol());
    }

    //stm sekce
    @Override
    public SymbolTable.Entry visitEmpty(ExprParser.EmptyContext ctx) {
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitDeclaration(ExprParser.DeclarationContext ctx) {
        var type = visit(ctx.type());

        for (var identifier : ctx.IDENTIFIER()) {
            symbolTable.add(identifier.getSymbol(), type.type);
        }
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
    public SymbolTable.Entry visitPrint(ExprParser.PrintContext ctx) {
        var value = visit(ctx.expr());
        if (value.type != Type.Error) {
//            System.out.println(value.value);
        }
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitRead(ExprParser.ReadContext ctx) {
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitWrite(ExprParser.WriteContext ctx) {
        for (var expr : ctx.expr()) {
            visit(expr);
        }
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitCode_stm(ExprParser.Code_stmContext ctx) {
        for (var statement : ctx.stm()) {
            visit(statement);
        }
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitIf(ExprParser.IfContext ctx) {
        var condition = visit(ctx.expr());
        if (condition.type != Type.Boolean) return new SymbolTable.Entry(Type.Error);
        visit(ctx.stm(0));
        if (ctx.stm().size() > 1) visit(ctx.stm(1));
        return new SymbolTable.Entry(Type.OK);
    }
    @Override
    public SymbolTable.Entry visitWhile(ExprParser.WhileContext ctx) {
        var condition = visit(ctx.expr());
        if (condition.type != Type.Boolean) return new SymbolTable.Entry(Type.Error);
        visit(ctx.stm());
        return new SymbolTable.Entry(Type.OK);
    }
}

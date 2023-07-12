grammar Expr;

prog: stm+;

stm: ';'                                    #empty
    | type IDENTIFIER(','IDENTIFIER)*';'    #declaration
    | expr';'                               #print
    | 'read' IDENTIFIER(','IDENTIFIER)*';'  #read
    | 'write' expr(','expr)*';'             #write
    | '{' stm+ '}'                          #code_stm
    | 'if (' expr ')' stm ('else' stm)?     #if
    | 'while(' expr ')' stm                 #while
    ;

type: type_v='int'
    | type_v='float'
    | type_v='string'
    | type_v='bool'
    ;

expr: IDENTIFIER                            #print_var
    | '(' expr ')'                          #para
    | '-'expr                               #uminus
    | '!'expr                               #not
    | expr op=('*'|'/') expr                #mul
    | expr '%' expr                         #mod
    | expr op=('+'|'-') expr                #add
    | expr '.' expr                         #concat
    | expr op=('<'|'>') expr                #rational
    | expr op=('=='|'!=') expr              #equals
    | expr '&&' expr                        #and
    | expr '||' expr                        #or
    | INT                                   #integer
    | <assoc=right> IDENTIFIER '=' expr     #assign
    | FLOAT                                 #float
    | ('true'|'false')                      #bool
    | STRING                                #string
    ;

IDENTIFIER : [a-zA-Z][a-zA-Z0-9]* ;
INT : [0-9]+ ;
FLOAT : [0-9]+'.'[0-9]+ ;
STRING : '"'(.)*?'"' ;
WS : [ \r\n\t]+ -> skip ;
COMMENT: '//'(.)*?[\r\n] -> skip ;
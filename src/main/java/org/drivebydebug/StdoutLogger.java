package org.drivebydebug;

import java.util.List;

public class StdoutLogger implements Logger {

   @Override
   public void onBreakpoint(Break breakpoint) {

        Io.echo("[" + breakpoint.getSourceName() + ":" + breakpoint.getLineNumber() + "]");
       for(Evaluation evaluation : breakpoint.evaluations()){
            String expression = evaluation.getExpression();
            String value = evaluation.getValueAString();
            Io.echo(expression + ":" + value);
       }
   }

    public void onError(Throwable ball){
        ball.printStackTrace();
    }

}
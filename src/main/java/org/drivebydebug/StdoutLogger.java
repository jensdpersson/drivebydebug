package org.drivebydebug;

public class StdoutLogger implements Logger {

   @Override
   public void onBreakpoint(Break breakpoint) {

        Io.echo("[" + breakpoint.getSourceName() + ":" + breakpoint.getLineNumber() + "]");
       for(Evaluation evaluation : breakpoint.evaluations()){
            String expression = evaluation.getExpression();
            Throwable up = evaluation.getFailure();
            if (up != null) {
                Io.echo("FAILED evaluating " + expression + " : " + up.getStackTrace().toString());
            }
            String value = evaluation.getValueAString();
            Io.echo(expression + ":" + value);
       }
   }

    public void onError(Throwable ball){
        ball.printStackTrace();
    }

}
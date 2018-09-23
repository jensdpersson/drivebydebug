package org.drivebydebug;

public class StdoutLogger implements Logger {

   @Override
   public void onEvaluation(Evaluation evaluation) {
       String expression = evaluation.getExpression();
       String value = evaluation.getValueAString();
       System.out.println(expression + ":" + value);
   }

    public void onError(Throwable ball){
        ball.printStackTrace();
    }

}
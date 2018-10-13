package org.drivebydebug;

import java.util.List;

public class StdoutLogger implements Logger {

   @Override
   public void onBreakpoint(List<Evaluation> evaluations) {
       for(Evaluation evaluation : evaluations){
             String expression = evaluation.getExpression();
            String value = evaluation.getValueAString();
            System.out.println(expression + ":" + value);
       }
   }

    public void onError(Throwable ball){
        ball.printStackTrace();
    }

}
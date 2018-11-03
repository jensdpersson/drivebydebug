# drivebydebug

## What is it?
Drive-By Debug is a command line tool that attaches to a jvm like a normal debugger, and pauses on breakpoints like a normal debugger. With a normal debugger a user would then click around a graphical gui to find the variable values of interest. With Drive-by Debug, the variables are configured in advance so when the jvm pauses the relevant values are immediately recorded and then the jvm is resumed as fast as possible.

## So when is this convenient?
- Some applications behave differently when timing changes because the debugger has paused on   break points. Timeouts can fire, connections can be lost. Just collecting values and then     resuming minimizes these effects.

- Sometimes it is convenient to check the same set of values at the same point in the code      for different invocations. Logging the values separately avoids the tedium of manually        tabulating results.

- Sometimes it is not practical or possible to connect a graphical debugger to a remote         machine where a jvm is running. A command line utility installable on the remote host can     be more convenient.

- If a behaviour that needs to be investigated by debugging occurs on a production server       then connecting a graphical debugger and pausing it may not be possible. Collecting values    on the fly with minimal pauses just may be.



## What state is it in?
Pretty early. 


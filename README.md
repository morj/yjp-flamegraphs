# yjp-flamegraphs

Utility for generating flame graphs from yjp snapshots
  
Tested with data generated from yjp version 2014

Usage:
  1. generate csv dump via `java -jar -Dexport.call.tree.cpu -Dexport.csv ~/yjp/lib/yjp.jar -export ~/Snapshots/abc.snapshot ~/TempFolderName/`
  2. run this program via `java -jar yjp-flamegraphs.jar [TempFolderName] [ThreadName] > /path/to/stacks-dump.txt`
     [ThreadName] is an optional parameter and can be omitted, in this case the flame graph will aggregate all samples
  3. generate flame graph with https://github.com/brendangregg/FlameGraph
     `./flamegraph.pl --countname=millis --width=1900 /path/to/stacks-dump.txt > /path/to/stacks.svg`
  
The steps 2-3 can be combined by piping the applications together:
     `java -jar ~/dev/yjp-flamegraphs.jar ~/Temp | ./flamegraph.pl --countname=millis --width=1900 > /path/to/stacks.svg`

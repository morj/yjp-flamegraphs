# yjp-flamegraphs

  Utility for generating flame graphs from yjp snapshots

  Usage:
  1. generate csv dump via `java -jar -Dexport.call.tree.cpu -Dexport.csv ~/yjp/lib/yjp.jar -export ~/Snapshots/abc.snapshot ~/TempFolderName/`
  2. run this program via `java -jar yjp-flamegraphs.jar [TempFolderName] [ThreadName] > /path/to/stacks-dump.txt`
  3. generate flame graph with https://github.com/brendangregg/FlameGraph
     `./flamegraph.pl --width=1900 /path/to/stacks-dump.txt > /path/to/stacks.svg`

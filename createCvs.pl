#!/usr/bin/perl

# ./92/build.xml:        <numberOfNewWarnings>0</numberOfNewWarnings>

$line = 0;
while (<>) {
    if ($line == 0) {
        /.*\/(\d+)\/.*>(\d+)</;
        $build = $1;
        $all = $2;
        $line++;
    }
    elsif ($line == 1) {
        /.*\/\d+\/.*>(\d+)</;
        $new = $1;
        $line++;
    }
    elsif ($line == 2) {
        /.*\/\d+\/.*>(\d+)</;
        $fixed = $1;
    	print "$build,$all,$new,$fixed\n";
    	$line = 0;
    }
}
#!/usr/bin/perl

# 338: commit  2d78b8c4d136f3d524ece5d11fd484031c6b454f

while (<>) {
    /\s*(\d+): commit\s+([0-9a-f]*)/;
    print "$1,$2\n";
}
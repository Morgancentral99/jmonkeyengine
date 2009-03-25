#!/bin/bash -p

PROGNAME="${0##*/}"

# $Id$
#
# You can feed command-line args to your script by supplying comand-line
# args to this program after "--".  Examples
#    path/to/blenderscript.bash   # Runs the script that you type in
#    path/to/blenderscript.bash script.py s2.py # Runs the specified scripts
#    path/to/blenderscript.bash script.py -- alpha beta
# Regarding this last example, your script will get a sys.argv list like:
#     ['./blender', '-P', '/tmp/blenderscript-24266.py', '--', 'one', 'two']
#
# By default will run in minimal size at the bottom left of your screen.
# (We request 1x1 pixel, but Blender makes it about 100x100 pixels or so).
# If you want to run with defaults (probably full-screen), export NOCOORDS=1.
# To set your own startup coords, export COORDS.
#
# This script takes the liberty of absolutizing the script paths that you
# specify.  This is because for a normal Blender installation, Blender will
# change your directory to the Blender directory when it starts up.  If we
# left relative paths as they are, they would be relative to the Blender
# installation root... and no good developer writes tests scripts there.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#
# * Redistributions of source code must retain the above copyright
#   notice, this list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright
#   notice, this list of conditions and the following disclaimer in the
#   documentation and/or other materials provided with the distribution.
#
# * Neither the name of the project nor the names of its contributors 
#   may be used to endorse or promote products derived from this software 
#   without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
# TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
# PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
# CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

shopt -s xpg_echo
set +u

Failout() {
    echo "Aborting $PROGNAME:  $*" 1>&2
    exit 1
}
[ -n "$TMPDIR" ] || TMPDIR=/tmp
TMPFILE="$TMPDIR/${PROGNAME%%.*}-$$.py"

declare -a scripts
while [ $# -gt 0 ]; do
    case "$1" in
       '--') break 2;;
        /*) scripts=("${scripts[@]}" "$1");;
        *) scripts=("${scripts[@]}" "${PWD}/$1");;
    esac
    shift
done
#echo ${#scripts[@]} scripts

type -t blender >&- || Failout 'Blender is not in your env search path'

# Purposefully not writing an interpreter line
echo -n "# Blender Python script generated by $PROGNAME at " >> "$TMPFILE" ||
Failout "Failed to write temp file '$TMPFILE'"

[ -n "$DEBUG" ] || trap "rm '$TMPFILE'" EXIT
# Retain the temp file for debugging or re-use purposes if env var set

date >> "$TMPFILE"
echo >> "$TMPFILE"
case ${#scripts[@]} in
    0) echo 'Enter script text:' 1>&2; cat;;
    *) for script in "${scripts[@]}"; do
           echo "execfile('$script')"
       done >> "$TMPFILE";;
esac >> "$TMPFILE"
echo 'from Blender import Quit\nQuit()' >> "$TMPFILE"

# If you want to run with defaults (probably full-screen), export NOCOORDS=1.
# To set your own startup coords, export COORDS.
if [ -n "$NOCOORDS" ]; then
    unset COORD_PARAMS
else
    [ -n "$COORDS" ] || COORDS=(1 1 1 1)
    COORD_PARAMS=(-p ${COORDS[*]})
fi

case "$0" in
/*) SCRIPTDIR="${0%/*}";; */*) SCRIPTDIR="$PWD/${0%/*}";; *) SCRIPTDIR="$PWD";;
esac
case "$SCRIPTDIR" in *?/.) SCRIPTDIR="${SCRIPTDIR%/.}"; esac

[ -z "$PYTHONPATH" ] && [ -d "${SCRIPTDIR%/*}/src" ] &&
export PYTHONPATH="${SCRIPTDIR%/*}/src"
# Put our dev script base directory into the Python search path.
# I don't yet know precedence.  In worse case, just uninstall the files you
# are working on from your normal Blender scripts directory.

#chmod +x "$TMPFILE"   Blender doesn't need to be executable, so safer without
[ -n "$VERBOSE" ] && echo blender "${COORD_PARAMS[@]}" -P "$TMPFILE" "$@" 1>&2
blender "${COORD_PARAMS[@]}" -P "$TMPFILE" "$@"
# Would prefer to exec blender, but that prevents the EXIT trap from working

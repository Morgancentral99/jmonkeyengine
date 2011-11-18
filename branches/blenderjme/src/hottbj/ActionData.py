__version__ = '$Revision$'
__date__ = '$Date$'
__author__ = 'Blaine Simpson, blaine (dot) simpson (at) admc (dot) com'
__url__ = 'http://www.jmonkeyengine.com'

# Copyright (c) 2009, Blaine Simpson and the jMonkeyEngine team
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#     * Neither the name of the <organization> nor the
#       names of its contributors may be used to endorse or promote products
#       derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY Blaine Simpson and the jMonkeyEngine team
# ''AS IS'' AND ANY
# EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL Blaine Simpson or the jMonkeyEngine team
# BE LIABLE FOR ANY
# DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

from bpy.data import scenes as _bScenes
import jme.esmath as _esmath
from Blender import Get as _bGet
from Blender import Set as _bSet

class ActionData(object):
    """
    This class encapsulates preparation of the data of a single Blender Action
    applied to a single Armature.

    Usage:  instantiate, addPose for all 'blenderFrames', then cull().
    The mats, locs, rots dictionaries are then good to use.

    The key sets for locs, rots, mats are always the same FOR NOW, so always
    use mats so we can eliminate the superfluous when time allows.

    .blenderFrames == None or .keyframeTimes == None means that the Action
    has no meaningful frames, and should therefore be discarded.
    """
    __slots__ = ('blenderFrames', 'keyframeTimes', 'locs', 'rots', 'mats',
            'name', 'boneMap', 'startFrame', 'endFrame', 'origBlenderFrame',
            'interpType', 'restPoseFrame', 'channels')
    # Not sure which of rots and/or mats will be used.
    # For now, using 'rots' just to cull <rotations> with no rotations.
    # boneMap is the map of the armature bones, not the pose bones.  We need
    # this because the pose bones don't keep enough nesting data.

    # TODO:  Reorganize so that addPose() just sets the mats, then call
    # nestBones just once for each bone, instead of once per bone per frame.

    frameRate = None

    def getChannelNames(self):
        return set(self.mats.keys())

    def getName(self):
        return self.name

    def __init__(self, bAction, boneMap):
        "CRITICALLY IMPORTANT to call this only when this action is 'active'"

        object.__init__(self)
        self.name = bAction.name
        self.restPoseFrame = None
        self.channels = None
        # Most frame index vars are key FRAME indexes starting at 0.
        # *BlenderFrame vars are absolute Blender frame number starting at 1.
        self.origBlenderFrame = _bGet("curframe")
        blenderStaFrame = _bGet("staframe")
        blenderEndFrame = _bGet("endframe")
        if blenderStaFrame > blenderEndFrame:
            raise Exception("Specified start frame for Action '" + self.name
                    + "' is after the specified end frame:  "
                    + str(blenderStaFrame)
                    + " vs. " + str(blenderEndFrame))
            # The Blender UI enforces this, but it doesn't hurt to be safe
        self.boneMap = boneMap
        if ActionData.frameRate == None:
            raise Exception("You can't instantiate ActionData until the "
                    + "frameRate is updated")

        # Initialize candidate loc and rot lists
        self.locs = {}
        self.rots = {}
        self.mats = {}
        self.interpType = None
        for boneName in bAction.getChannelNames():
            self.locs[boneName] = []
            self.rots[boneName] = []
            self.mats[boneName] = []

        # Purpose of this block is to get a set of the SIGNIFICANT frames,
        # 'blenderFrames'
        frameSet = set()
        for ipos in bAction.getAllChannelIpos().itervalues():
            if ipos == None or len(ipos) < 1: continue
            for curve in ipos:
                # There are normally 7 curves for an IPO.
                # However, if there is a Quat IPO, all 4 Quat* will be present,
                # and if there is a Loc IP, all 3 Loc* will be present.
                # Therefore, just test one of each.
                if curve.name not in ['LocX', 'QuatW']: continue
                if self.interpType == None:
                    self.interpType = curve.interpolation
                else:
                    if self.interpType != curve.interpolation:
                        raise Exception("Animation '" + self.getName()
                                + "' mixes animation types.  "
                                + "This is not supported.")
                for bp in curve.bezierPoints:
                    frameFloat = bp.vec[1][0]
                    if frameFloat < 1:
                        print("Discarding off-the-chart bezier frame value "
                                + str(frameFloat)
                                + " from anim '" + self.name + "'.")
                        continue
                    # Remember that Blender frames are 1-based, not 0-based
                    # This number is usually just the float form of an integer,
                    # but if user has copied and moved an action, it can be
                    # non-integral.
                    frameSet.add(frameFloat)
                    # Leaving value a float so float division will occur below
        if len(frameSet) < 1:
            self.blenderFrames = None
            self.keyframeTimes = None
            return
        self.blenderFrames = list(frameSet)
        self.blenderFrames.sort()
        self.keyframeTimes = []
        self.startFrame = None
        self.endFrame = None
        keyFrameNum = -1
        for blenderFrameNum in self.blenderFrames[:]:
            keyFrameNum += 1
            if self.startFrame == None:
                if blenderStaFrame == blenderFrameNum:
                    self.startFrame = keyFrameNum
                elif blenderStaFrame < blenderFrameNum:
                    self.startFrame = keyFrameNum
                    self.blenderFrames.insert(keyFrameNum, blenderStaFrame)
                    self.keyframeTimes.append(
                            (blenderStaFrame-1.) / ActionData.frameRate)
                    keyFrameNum += 1
            if self.endFrame == None:
                if blenderEndFrame == blenderFrameNum:
                    self.endFrame = keyFrameNum
                elif blenderEndFrame < blenderFrameNum:
                    self.endFrame = keyFrameNum
                    self.blenderFrames.insert(keyFrameNum, blenderEndFrame)
                    self.keyframeTimes.append(
                            (blenderEndFrame-1.) / ActionData.frameRate)
                    keyFrameNum += 1
            self.keyframeTimes.append(
                    (blenderFrameNum-1.) / ActionData.frameRate)
        if self.startFrame == None:
            raise Exception("Start frame for Action '" + self.name
                    + "' is set to frame after end of animation: "
                    + str(blenderStaFrame))
        if self.endFrame == None: self.endFrame = len(self.blenderFrames) - 1
        # We do not allow extrapolating past end of keyframes, because this
        # is the (poor) default that Blender sets, yet this is not what users
        # usually want.  The default should be to stop at the last key frame.

    def reset(self, pose):
        """CRITICALLY IMPORTANT to call this only when this action is 'active'
        Restores current frame number and sets rest pose for channels."""
        _bSet("curframe", self.origBlenderFrame)
        ActionData.zeroPose(pose)

    def addPose(self, poseBones):
        "Add a pose for a single frame"
        # First we get concentrate on setting the last mats element for each
        # bone, then we go back and set the last locs and rots elements,
        # based on the new mats element.
        allRests = self.restPoseFrame == None
        for boneName in self.mats.iterkeys():  # equivalent to self.rots...
            # TODO:  IF this is a Blender key frame (not a generated start or
            # end keyframe), then it would be better to not store the channel
            # if the channel is not present in the keyframe, so that jME can
            # interpolate, just like it happens in Blender.
            if boneName in poseBones.keys():
                self.mats[boneName].append(poseBones[boneName].poseMatrix.copy())
                if allRests and not _esmath.isIdentity(
                        poseBones[boneName].localMatrix): allRests = False
            else:
                raise Exception(
                        "Internal error: Channel has no val for a frame, "
                        + "in Action '" + self.name + "'");
        if allRests: self.restPoseFrame = len(self.mats[self.mats.keys()[0]])-1

        for boneName in self.mats.iterkeys():
            if poseBones[boneName].parent == None:
                matInv = self.mats[boneName][-1].copy().invert()
                for childBone in self.boneMap[boneName].children:
                    self.applyMat(childBone, matInv)

        for boneName in self.mats.iterkeys():
            locs = self.locs[boneName]
            rots = self.rots[boneName]
            mat = self.mats[boneName][-1]
            if boneName in poseBones.keys():
                locs.append(mat.translationPart())
                rots.append(mat.rotationPart().toQuat())
                if not _esmath.floatsEq(mat.scalePart(), 1.):
                    raise Exception(
                    "Remove bone scalings from anim '" + self.getName()
                    + "'.  They are not compatible with jME.");
            else:
                raise Exception(
                        "Internal error: Channel data gone missing in Action '"
                        + self.name + "'");

    def applyMat(self, armaBone, matInv):
        """Updates the matrixes of all descendant Pose Bones (not Armature Bones)
        to account for the matrixes of ancestor bones."""

        if armaBone.name not in self.mats: return
        self.mats[armaBone.name][-1] *= matInv
        if armaBone.children == None: return
        newInv = matInv * self.mats[armaBone.name][-1].copy().invert()
        for childBone in armaBone.children:
            #print childBone.name + " childof " + armaBone.name
            self.applyMat(childBone, newInv)

    def cull(self):
        # We are not culling any frames here, only channels.
        # The designer creates frames where he wantes them, therefore we honor
        # the frames.
        if self.blenderFrames == None: return
        usedRotChannels = set()
        usedLocChannels = set()
        for boneName, oneBoneRots in self.rots.iteritems():
            for quat in oneBoneRots:
                if (round(quat.x, 6) != 0 or round(quat.y, 6) != 0
                        or round(quat.z, 6) != 0 or round(quat.w) != 1):
                    usedRotChannels.add(boneName)
        for boneName, oneBoneLocs in self.locs.iteritems():
            for loc in oneBoneLocs:
                if not _esmath.floatsEq(loc, 0.):
                    usedLocChannels.add(boneName)

        # It is likely that we can remove items from just the loc or the rot
        # list, but until we get far enough along to test this, we must be
        # safe and assume that a non-zero raw loc could produced derived
        # non-zero rot; and that a non-zero raw rot could produce derived
        # non-zero loc.
        for zapBone in (
            set(self.mats.keys()) - (usedRotChannels | usedLocChannels)):
                print ("Zapping unused channel '" + zapBone
                        + "' from animation '" + self.getName() + "'.")
                del self.rots[zapBone]
                del self.locs[zapBone]
                del self.mats[zapBone]
        if len(self.mats) < 1:
            print "No significant channels for Action '" + self.name + "'."
            self.blenderFrames = None
            self.keyframeTimes = None
        self.channels = len(self.mats)
        return   # READ COMMENT ABOVE about the more liberal culling below.

        for rotZapBone in set(self.boneRots.keys()) - usedRotChannels:
            print("Zapping bone '" + rotZapBone + "' from Action '"
                    + self.name + "' rotations")
            del self.rots[rotZapBone]
        for locZapBone in set(self.boneLocs.keys()) - usedLocChannels:
            print("Zapping bone '" + locZapBone + "' from Action '"
                    + self.name + "' translations")
            del self.locs[locZapBone]
            if locZapBone not in set(self.bonRots.keys()):
                del self.mats[locZapBone]

    def updateFrameRate():
        ActionData.frameRate =  \
                _bScenes.active.getRenderingContext().framesPerSec()

    def zeroPose(pose):
        for poseBone in pose.bones.values(): poseBone.localMatrix.identity()
        # When I do this interactively, to see results in 3DView, need to run
        # scene.update(1) plus Blender.redraw().  But here, the same update
        # seems to invalidate the identity setting. ??
        #_bScenes.active.update(1)
        #pose.update()   # Docs say this is needed, but never helped me.

    updateFrameRate = staticmethod(updateFrameRate)
    zeroPose = staticmethod(zeroPose)
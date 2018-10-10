package com.example.radio;

/**
 * Created by zhangheng1 on 2018/7/31.
 */

        import android.hardware.Camera.CameraInfo;
        import android.media.CamcorderProfile;
        import android.media.MediaRecorder;
        import android.util.Log;

public class Profile {

    private static final String TAG = "Profile";

    private CamcorderProfile mProfile;
    private MzCamcorderProfile mMzProfile;
    private boolean mIsUseMzProfile;

    public void initCamcorderProfile(int cameraId, int quality) {
        try {
            mIsUseMzProfile = false;
            Log.d(TAG, "cameraId = " + cameraId + ", quality = " + quality);
            mProfile = CamcorderProfile.get(cameraId, quality);
        } catch (Exception e) {
            Log.d(TAG, "getCamProfile failed : " + e);
            if (quality == 2003) {
                mIsUseMzProfile = true;
                mMzProfile = new MzCamcorderProfile(60,
                        2003, MediaRecorder.OutputFormat.MPEG_4,
                        MediaRecorder.VideoEncoder.H264,
                        14000000,
                        25,
                        1280,
                        720,
                        -1,
                        -1,
                        -1,
                        -1);
            } else {
                mIsUseMzProfile = false;
            }
        }
    }

    public CamcorderProfile getCamcorderProfile() {
        return mProfile;
    }

    public int getVideoFrameWidth() {
        if (mIsUseMzProfile) {
            return mMzProfile.videoWidth;
        }
        if (mProfile == null) {
            return -1;
        }
        return mProfile.videoFrameWidth;
    }

    public int getVideoFrameHeight() {
        if (mIsUseMzProfile) {
            return mMzProfile.videoHeight;
        }
        if (mProfile == null) {
            return -1;
        }
        return mProfile.videoFrameHeight;
    }

    public int getVideoFrameRate() {
        if (mIsUseMzProfile) {
            return mMzProfile.videoFrameRate;
        }
        return mProfile.videoFrameRate;
    }

    public int getFileFormat() {
        if (mIsUseMzProfile) {
            return mMzProfile.fileFormat;
        }
        return mProfile.fileFormat;
    }

    public int getVideoBitRate() {
        if (mIsUseMzProfile) {
            return mMzProfile.videoBitRate;
        }
        return mProfile.videoBitRate;
    }

    public int getVideoEncoder() {
        if (mIsUseMzProfile) {
            return mMzProfile.videoCodec;
        }
        return mProfile.videoCodec;
    }

    public int getVideoDuration() {
        if (mIsUseMzProfile) {
            return mMzProfile.duration;
        }
        return mProfile.duration;
    }

    public int getVideoQuality() {
        if (mIsUseMzProfile) {
            return mMzProfile.quality;
        }
        return mProfile.quality;
    }

    public int getAudioCodec() {
        if (mIsUseMzProfile) {
            return mMzProfile.audioCodec;
        }
        return mProfile.audioCodec;
    }

    public int getAudioBitRate() {
        if (mIsUseMzProfile) {
            return mMzProfile.audioBitRate;
        }
        return mProfile.audioBitRate;
    }

    public int getAudioSampleRate() {
        if (mIsUseMzProfile) {
            return mMzProfile.audioSampleRate;
        }
        return mProfile.audioSampleRate;
    }

    public int getAudioChannels() {
        if (mIsUseMzProfile) {
            return mMzProfile.audioChannels;
        }
        return mProfile.audioChannels;
    }

    public float getVideoRatio() {
        if (mIsUseMzProfile) {
            return (float) mMzProfile.videoWidth / mMzProfile.videoHeight;
        }
        return (float) mProfile.videoFrameWidth / mProfile.videoFrameHeight;
    }

    public void setProfile(MediaRecorder recorder) {
        if (recorder != null) {
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setOutputFormat(mMzProfile.fileFormat);
            recorder.setVideoFrameRate(mMzProfile.videoFrameRate);
            recorder.setVideoSize(mMzProfile.videoWidth, mMzProfile.videoHeight);
            recorder.setVideoEncodingBitRate(mMzProfile.videoBitRate);
            recorder.setVideoEncoder(mMzProfile.videoCodec);
            recorder.setAudioEncodingBitRate(mMzProfile.audioBitRate);
            recorder.setAudioChannels(mMzProfile.audioChannels);
            recorder.setAudioSamplingRate(mMzProfile.audioSampleRate);
            recorder.setAudioEncoder(mMzProfile.audioCodec);
        }
    }

    private class MzCamcorderProfile {

        public int duration;
        public int quality;
        public int fileFormat;
        public int videoCodec;
        public int videoBitRate;
        public int videoFrameRate;
        public int videoWidth;
        public int videoHeight;
        public int audioCodec;
        public int audioBitRate;
        public int audioSampleRate;
        public int audioChannels;

        public MzCamcorderProfile(int duration, int quality, int fileFormat, int videoCodec,
                                  int videoBitRate, int videoFrameRate, int videoWidth, int videoHeight,
                                  int audioCodec, int audioBitRate, int audioSampleRate, int audioChannels) {
            this.duration = duration;
            this.quality = quality;
            this.fileFormat = fileFormat;
            this.videoCodec = videoCodec;
            this.videoBitRate = videoBitRate;
            this.videoFrameRate = videoFrameRate;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.audioCodec = audioCodec;
            this.audioBitRate = audioBitRate;
            this.audioSampleRate = audioSampleRate;
            this.audioChannels = audioChannels;
        }
    }

    public static boolean hasProfile(int cameraId, int quality) {
        if (cameraId == CameraInfo.CAMERA_FACING_BACK) {
            if (quality != -1 && quality == 0)
                return true;
        }
        Log.d(TAG, "hasProfile cameraId = " + cameraId + ", quality = " + quality
                + ", is have this profile: " + CamcorderProfile.hasProfile(cameraId, quality));
        return CamcorderProfile.hasProfile(cameraId, quality);
    }

}

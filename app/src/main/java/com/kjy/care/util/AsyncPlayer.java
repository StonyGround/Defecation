package com.kjy.care.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import java.io.IOException;
import java.util.LinkedList;

public class AsyncPlayer {
    private static final int PLAY = 1;
    private static final int STOP = 2;
    private LinkedList mCmdQueue = new LinkedList();
    private String mTag;
    private AsyncPlayer.Thread mThread;
    private MediaPlayer mPlayer;
    private WakeLock mWakeLock;
    private int mState = 2;

    private void startSound(AsyncPlayer.Command cmd) {
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(cmd.stream);
            player.setDataSource(cmd.context, cmd.uri);
            player.setLooping(cmd.looping);
            player.prepare();
            player.start();
            if (this.mPlayer != null) {
                this.mPlayer.release();
            }

            this.mPlayer = player;
        } catch (IOException var3) {
            Log.w(this.mTag, "error loading sound for " + cmd.uri, var3);
        } catch (IllegalStateException var4) {
            Log.w(this.mTag, "IllegalStateException (content provider died?) " + cmd.uri, var4);
        }

    }

    public AsyncPlayer(String tag) {
        if (tag != null) {
            this.mTag = tag;
        } else {
            this.mTag = "AsyncPlayer";
        }

    }

    public void play(Context context, Uri uri, boolean looping, int stream) {
        AsyncPlayer.Command cmd = new AsyncPlayer.Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.stream = stream;
        synchronized(this.mCmdQueue) {
            this.enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void stop() {
        synchronized(this.mCmdQueue) {
            if (this.mState != 2) {
                AsyncPlayer.Command cmd = new AsyncPlayer.Command();
                cmd.requestTime = SystemClock.uptimeMillis();
                cmd.code = 2;
                this.enqueueLocked(cmd);
                this.mState = 2;
            }

        }
    }

    private void enqueueLocked(AsyncPlayer.Command cmd) {
        this.mCmdQueue.add(cmd);
        if (this.mThread == null) {
            this.acquireWakeLock();
            this.mThread = new AsyncPlayer.Thread();
            this.mThread.start();
        }

    }

    public void setUsesWakeLock(Context context) {
        if (this.mWakeLock == null && this.mThread == null) {
            @SuppressLint("WrongConstant")
            PowerManager pm = (PowerManager)context.getSystemService("power");
            this.mWakeLock = pm.newWakeLock(1, this.mTag);
        } else {
            throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
        }
    }

    private void acquireWakeLock() {
        if (this.mWakeLock != null) {
            this.mWakeLock.acquire();
        }

    }

    private void releaseWakeLock() {
        if (this.mWakeLock != null) {
            this.mWakeLock.release();
        }

    }

    private final class Thread extends java.lang.Thread {
        Thread() {
            super("AsyncPlayer-" + AsyncPlayer.this.mTag);
        }

        public void run() {
            while(true) {
                AsyncPlayer.Command cmd = null;
                synchronized(AsyncPlayer.this.mCmdQueue) {
                    cmd = (AsyncPlayer.Command)AsyncPlayer.this.mCmdQueue.removeFirst();
                }

                switch(cmd.code) {
                    case 1:
                        AsyncPlayer.this.startSound(cmd);
                        break;
                    case 2:
                        if (AsyncPlayer.this.mPlayer != null) {
                            AsyncPlayer.this.mPlayer.stop();
                            AsyncPlayer.this.mPlayer.release();
                            AsyncPlayer.this.mPlayer = null;
                        } else {
                            Log.w(AsyncPlayer.this.mTag, "STOP command without a player");
                        }
                }

                synchronized(AsyncPlayer.this.mCmdQueue) {
                    if (AsyncPlayer.this.mCmdQueue.size() == 0) {
                        AsyncPlayer.this.mThread = null;
                        AsyncPlayer.this.releaseWakeLock();
                        return;
                    }
                }
            }
        }
    }

    private static final class Command {
        int code;
        Context context;
        Uri uri;
        boolean looping;
        int stream;
        long requestTime;

        private Command() {
        }

        public String toString() {
            return "{ code=" + this.code + " looping=" + this.looping + " stream=" + this.stream + " uri=" + this.uri + " }";
        }
    }
}
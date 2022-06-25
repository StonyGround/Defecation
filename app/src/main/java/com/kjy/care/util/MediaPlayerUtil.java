package com.kjy.care.util;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;




import com.kjy.care.activity.BaseApp;




public class MediaPlayerUtil {

    private static boolean playing =false;




    public static void play(String assetsFileName){


        MediaPlayer player =null;


        try{

            if(playing){
                return;
            }
            playing=true;
            player = new MediaPlayer();
            AssetFileDescriptor fd = BaseApp.getAppContext().getAssets().openFd(assetsFileName);
            player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
           player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    playing = false;

                    mediaPlayer.release();

                    LogH.e("播放完毕");


                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                    playing = false;
                    mediaPlayer.release();
                    LogH.e("播放错误=>"+i+"======>"+ i1);

                    return false;
                }
            });



        }catch (Exception e){
            playing=false;
            Log.e("播放异常","==>"+e.getMessage());

        }
    }

    public static  MediaPlayer player =null;
    /**
     *  String mp3 ="http://fanyi.baidu.com/gettts?lan=zh&spd=4&source=web&text="+ weatherContent;
     * 部分机型没法使用，问题出现在文件已损坏
     * @param rid
     */
    public static void play(int rid){





        try{


          //  playing=true;

            if(player == null) {
                player = new MediaPlayer();
            }
            Log.e("connectPlay","playing===>"+player.isPlaying());
            if(player.isPlaying()){
                return;
            }
            playing = true;
            Uri uri = Uri.parse("android.resource://" + BaseApp.getPackgeName() + "/" + rid);
           /* @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            AudioAttributes attributes = new  AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
            player.setAudioAttributes(attributes);*/
            player.setDataSource(BaseApp.getAppContext(),uri);
            player.prepareAsync();


            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mediaPlayer.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    playing = false;

                    mediaPlayer.release();
                    player=null;

                    LogH.e("播放完毕");


                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                    playing = false;
                    mediaPlayer.release();
                    player=null;
                    LogH.e("播放错误=>"+i+"======>"+ i1);

                    return false;
                }
            });



        }catch (Exception e){
            playing=false;
            Log.e("播放异常","==>"+e.getMessage());

        }
    }




  /*  public  static  void stop(){
        if(player!=null){
            player.stop();
            player.reset();;
            player.release();
            player=null;

        }
    }*/



    /**
     * 打开assets下的音乐mp3文件
     */
    private void openAssetMusics() {

        try {
            //播放 assets/a2.mp3 音乐文件
            /*AssetFileDescriptor fd = getAssets().openFd("identification_success.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

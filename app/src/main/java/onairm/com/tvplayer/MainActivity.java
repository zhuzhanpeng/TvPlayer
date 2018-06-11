package onairm.com.tvplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.onairm.tvbaselibrary.utils.CrashHandler;
import com.onairm.tvbaselibrary.utils.SinglePlayerUtil;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity{

    @Override
    public void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashHandler.getInstance().init(getApplicationContext(),"");

        startActivity(new Intent(MainActivity.this,PlayerDemoActivity.class));
    }
    public void onResume() {
        super.onResume();
        IjkMediaPlayer mediaPlayer = SinglePlayerUtil.getPlayer();
        if (null != mediaPlayer) {
            mediaPlayer.pause();
            mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    iMediaPlayer.pause();
                }
            });
        }
    }
}

package onairm.com.tvplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.onairm.tvbaselibrary.player.AbstractTvPlayerView;
import com.onairm.tvbaselibrary.player.LoadingNewPlayerView;
import com.onairm.tvbaselibrary.player.PlayerFactory;
import com.onairm.tvbaselibrary.player.SimpleFSActivity;
import com.onairm.tvbaselibrary.utils.PlayerBean;
import com.onairm.tvbaselibrary.utils.SinglePlayerUtil;

public class PlayerDemoActivity extends AppCompatActivity {

    private AbstractTvPlayerView fullScreenPlayer;
    private FrameLayout flPlayerContainer;
    private Button btnPlay;

    private String path="http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4";
    private String path2="http://jzvd.nathen.cn/35b3dc97fbc240219961bd1fccc6400b/8d9b76ab5a584bce84a8afce012b72d3-5287d2089db37e62345123a1be272f8b.mp4";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_demo);

        flPlayerContainer=findViewById(R.id.flPlayerContainer);
        flPlayerContainer.requestFocus();
        btnPlay=findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleFSActivity.jumpToLiveActivity(PlayerDemoActivity.this,
                        AbstractTvPlayerView.NEW_PLAYER,
                        path2,
                        "直接全屏播放", 0);
            }
        });

        btnPlay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    btnPlay.setBackgroundColor(Color.parseColor("#ff0000"));
                }else{
                    btnPlay.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
        });

        initPlayer();

    }
    private void initPlayer() {
        fullScreenPlayer = PlayerFactory.getInstance().newInstance(LoadingNewPlayerView.class,this);

        fullScreenPlayer.setPlayCompleteListener(new AbstractTvPlayerView.PlayerComplete() {
            @Override
            public void playComplete() {

            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        flPlayerContainer.addView(fullScreenPlayer, params);
        flPlayerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SimpleFSActivity.jumpToLiveActivity(PlayerDemoActivity.this,
                            AbstractTvPlayerView.NO_NEW_PLAYER,
                            path,
                            "小窗进全屏"
                            , PlayerDemoActivity.this.hashCode());
                }


        });

    }
    //离开页面保存对应Activity的PlayerBean,resumeActivity恢复现场
    @Override
    protected void onPause() {
        super.onPause();
        if (null != fullScreenPlayer) {
            fullScreenPlayer.pause();
            PlayerBean playerBean = SinglePlayerUtil.getPlayerBean(PlayerDemoActivity.this.hashCode());
            if (null == playerBean) {
                PlayerBean playerBean2 = new PlayerBean();
                playerBean2.setProgress(fullScreenPlayer.getProgress());
                SinglePlayerUtil.setPlayerBean(PlayerDemoActivity.this.hashCode(), playerBean2);
            } else {
                playerBean.setProgress(fullScreenPlayer.getProgress());
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (null != fullScreenPlayer) {
            PlayerBean playerBean = SinglePlayerUtil.getPlayerBean(PlayerDemoActivity.this.hashCode());
            if (null != playerBean) {
                long progress = playerBean.getProgress();
                fullScreenPlayer.recoverVideo(path, progress);
            }else {
                fullScreenPlayer.recoverVideo(path,0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fullScreenPlayer) {
            fullScreenPlayer.release();
            fullScreenPlayer.setStopLoad(true);
        }
    }
}

package net.arvin.pictureselectordemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.arvin.pictureselector.entities.ImageFolderEntity;
import net.arvin.pictureselector.models.ImagesModel;
import net.arvin.pictureselector.uis.PictureSelectorActivity;
import net.arvin.pictureselector.uis.adapters.PictureSelectorAdapter;

import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final long time = System.currentTimeMillis();
//        ImagesModel.getImageFolders(this).subscribe(new Action1<List<ImageFolderEntity>>() {
//            @Override
//            public void call(List<ImageFolderEntity> imageFolder) {
//                Log.i("getImageFolders",imageFolder.size()+"time:"+(System.currentTimeMillis()-time));
//
//            }
//        });
//        ImagesModel.getImageFoldersByMap(this).subscribe(new Action1<List<ImageFolderEntity>>() {
//            @Override
//            public void call(List<ImageFolderEntity> imageFolder) {
//                Log.i("getImageFoldersByMap",imageFolder.size()+"time:"+(System.currentTimeMillis()-time));
//            }
//        });

        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PictureSelectorActivity.class));
            }
        });
    }
}

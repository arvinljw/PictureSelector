package net.arvin.pictureselectordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSConstanceUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_1 = 1001;
    private ArrayList<ImageEntity> selectedImages;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImages = new ArrayList<>();

        PSConfigUtil.getInstance().setCanCrop(true)
                .setCanTakePhoto(true)
                .setMaxCount(1);

        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PSConfigUtil.getInstance().showSelector(MainActivity.this, REQUEST_CODE_1, selectedImages);
            }
        });

        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建议异步操作,如果文件过多则同样耗时
                PSConfigUtil.clearCache();
                Toast.makeText(MainActivity.this, "清除成功~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_1:
                    List<ImageEntity> temp = data.getParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED);
                    selectedImages.addAll(temp);
                    for (ImageEntity selectedImage : selectedImages) {
                        Log.d("back_data", selectedImage.getPath());
                    }
                    break;
            }
        }
    }
}

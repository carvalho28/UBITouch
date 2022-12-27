package pt.ubi.di.pdm.ubitouch;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class Configs {
    public static boolean isConfigInitialized = false;
    // Cloudinary
    public static final String CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dvo3vlsl3/image/upload";
    public static String CLOUD_NAME = "dvo3vlsl3";
    public static  String UPLOAD_PRESET = "ubitouch";


    public static void initConfig(Context context){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", "966681439871748");
        config.put("api_secret", "QYlGWWg5A9I7JSt4D0r4GXdnG6w");
        // config.put("secure", true);
        MediaManager.init(context, config);
        isConfigInitialized = true;
    }
}

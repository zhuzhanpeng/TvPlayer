package com.onairm.tvbaselibrary.giftext;


import com.onairm.tvbaselibrary.R;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 
 * @author tracyZhang  https://github.com/TracyZhangLei
 * @since  2014-4-4
 *
 */
public class FaceManager {
	
	private FaceManager(){
		initFaceMap();
	}
	
	private static FaceManager instance;

	public static FaceManager getInstance() {
		if(null == instance)
			instance = new FaceManager();
		return instance;
	}
	
	private Map<String, Integer> mFaceMap;
	
	private void initFaceMap() {
		mFaceMap = new LinkedHashMap<String, Integer>();
		mFaceMap.put("[音频]",R.raw.audio24);
	}
	
	public int getFaceId(String faceStr){
		if(mFaceMap.containsKey(faceStr)){
			return mFaceMap.get(faceStr);
		}
		return -1;
	}

}

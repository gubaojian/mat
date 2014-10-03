package com.efurture.mem;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 内存泄露处理工具，对于存在内存泄露的对象，调用WeakCallback的onWeak方法处理内存泄露。
 * 如果对象个数超过指定个数或者在调用clear时对象未被系统回收，则认为其为内存泄露，调用WeakCallback的onWeak方法处理内存泄露。
 * 通知开发者处理内存泄露
 * */
public class Mat {
	
	private LinkedList<WeakReference<Object>> weakReferenceQueue;
	private WeakCallback weakCallback;
	private int size;
	public Mat(){
		this(512);
	}
	
	public Mat(int size){
		this.size = size;
		weakReferenceQueue = new LinkedList<WeakReference<Object>>();
	}
	
    public void enter(Object object){
    	while (weakReferenceQueue.size() > size) {
    		WeakReference<Object> weakReference = weakReferenceQueue.poll();
    		Object objectRef = weakReference.get();
			if (objectRef == null) {
				continue;
			}
			if (weakCallback != null) {
				weakCallback.onWeak(object);
			}
		}
    	weakReferenceQueue.add(new WeakReference<Object>(object));
    }
    
    public void clear(){
		for (WeakReference<Object>  weakReference : weakReferenceQueue) {
			Object object = weakReference.get();
			if (object == null) {
				continue;
			}
			if (weakCallback != null) {
				weakCallback.onWeak(object);
			}
		}
		weakReferenceQueue.clear();
	}
    
    public static Mat from(String module){
    	Mat mat = matMap.get(module);
    	if (mat == null) {
    		synchronized (Mat.class) {
                mat = new Mat();
    			matMap.put(module, mat);
			}
		}
    	return mat;
    }
    
    public static void clear(String module){
    	Mat mat = matMap.remove(module);
    	if (mat != null) {
			mat.clear();
		}
    }
    
    private static final Map<String, Mat> matMap = new HashMap<String, Mat>();
	
}
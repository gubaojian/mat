package com.efurture.mem;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * �ڴ�й¶�����ߣ����ڴ����ڴ�й¶�Ķ��󣬵���WeakCallback��onWeak���������ڴ�й¶��
 * ��������������ָ�����������ڵ���clearʱ����δ��ϵͳ���գ�����Ϊ��Ϊ�ڴ�й¶������WeakCallback��onWeak���������ڴ�й¶��
 * ֪ͨ�����ߴ����ڴ�й¶
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
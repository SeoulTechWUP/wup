package wup.api;

import com.google.gson.Gson;

/**
 * Singleton {@link com.google.gson.Gson} 개체를 가져올 수 있는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class GsonHolder {
    private GsonHolder() {
    }

    public static Gson getGson() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final Gson INSTANCE = new Gson();
    }
}

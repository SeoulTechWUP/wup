package wup.api;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * REST API 처리 중 발생한 오류를 나타내는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Error {
    // 1xx: 사용자 인증 관련 오류
    public static final int E_NOAUTH = 100;
    public static final int E_NOACCESS = 101;
    public static final int E_EMAILDUP = 102;
    public static final int E_LOGINFAIL = 103;
    public static final int E_ACCDELREFUSED = 104;
    public static final int E_PWMISMATCH = 105;

    // 2xx: 플래너 관련 오류
    public static final int E_PLADELREFUSED = 201;

    // 3xx: 일정 관련 오류
    public static final int E_BADDATERANGE = 300;

    // 8xx: API 매개변수 오류
    public static final int E_ARGMISSING = 800;
    public static final int E_BADARG = 801;

    // 9xx: 서버 오류
    public static final int E_DBERROR = 900;
    public static final int E_NOENT = 901;
    public static final int E_NOIMPL = 999;

    public static final Map<Integer, String> messages;

    static {
        messages = new HashMap<>();

        // 1xx: 사용자 인증 관련 오류
        messages.put(E_NOAUTH, "사용자 인증이 필요합니다.");
        messages.put(E_NOACCESS, "이 항목에 접근할 권한이 없습니다.");
        messages.put(E_EMAILDUP, "중복된 이메일 주소입니다.");
        messages.put(E_LOGINFAIL, "이메일 주소 또는 암호를 확인해 주세요.");
        messages.put(E_ACCDELREFUSED, "계정 삭제 확인이 필요합니다.");
        messages.put(E_PWMISMATCH, "기존 암호가 일치하지 않습니다.");

        // 2xx: 플래너 관련 오류
        messages.put(E_PLADELREFUSED, "플래너 삭제 확인이 필요합니다.");

        // 3xx: 일정 관련 오류
        messages.put(E_BADDATERANGE, "endsAt은 startsAt보다 앞에 올 수 없습니다.");

        // 8xx: API 매개변수 오류
        messages.put(E_ARGMISSING, "필요한 매개변수가 제공되지 않았습니다.");
        messages.put(E_BADARG, "매개변수 형식이 잘못되었습니다.");

        // 9xx: 서버 오류
        messages.put(E_DBERROR, "데이터베이스 오류가 발생했습니다.");
        messages.put(E_NOENT, "요청한 항목을 찾을 수 없습니다.");
        messages.put(E_NOIMPL, "이 기능은 아직 구현되지 않았습니다.");
    }

    private final int code;
    private final String message;

    public Error(int code) {
        String message = messages.get(code);

        this.code = code;
        this.message = message == null ? "알 수 없는 오류입니다." : message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        Gson gson = GsonHolder.getGson();
        JsonObject jObj = new JsonObject();

        jObj.add("error", gson.toJsonTree(this));

        return gson.toJson(jObj);
    }
}

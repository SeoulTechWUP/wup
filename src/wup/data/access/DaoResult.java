package wup.data.access;

/**
 * DAO의 처리 결과를 나타내는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public final class DaoResult<T> {
    /**
     * DAO가 수행한 작업의 유형을 나타내는 열거형입니다.
     *
     * @author Eunbin Jeong
     */
    public enum Action {
	/**
	 * DAO가 생성 작업을 수행했습니다.
	 */
	CREATE,
	/**
	 * DAO가 읽기 작업을 수행했습니다.
	 */
	READ,
	/**
	 * DAO가 갱신 작업을 수행했습니다.
	 */
	UPDATE,
	/**
	 * DAO가 삭제 작업을 수행했습니다.
	 */
	DELETE
    }

    private final Action performedAction;
    private final boolean succeeded;
    private final T data;
    private final Exception exception;

    public static <U> DaoResult<U> succeed(Action performedAction, U data) {
	return new DaoResult<U>(performedAction, true, data, null);
    }

    public static <U> DaoResult<U> fail(Action performedAction, Exception exception) {
	return new DaoResult<U>(performedAction, false, null, exception);
    }

    private DaoResult(Action performedAction, boolean succeeded, T data, Exception exception) {
	this.performedAction = performedAction;
	this.succeeded = succeeded;
	this.data = data;
	this.exception = exception;
    }

    /**
     * DAO가 수행한 작업의 유형을 가져옵니다.
     */
    public Action getPerformedAction() {
	return performedAction;
    }

    /**
     * DAO가 작업을 성공적으로 수행했는지 여부를 가져옵니다.
     */
    public boolean didSucceed() {
	return succeeded;
    }

    /**
     * DAO가 작업을 성공적으로 수행한 경우, 작업의 결과를 가져옵니다.
     * @return 작업이 실패한 경우 <code>null</code>을 반환합니다.
     */
    public T getData() {
	return data;
    }

    /**
     * DAO가 작업을 수행하는 도중 예외가 발생한 경우, 발생한 예외에 대한 인스턴스를 가져옵니다.
     * @return 작업이 성공적으로 완료된 경우 <code>null</code>을 반환합니다.
     */
    public Exception getException() {
	return exception;
    }
}

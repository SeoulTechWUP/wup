package wup.data.access;

import java.util.List;

import wup.data.ItemOwner;
import wup.data.Planner;

/**
 * 플래너 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface PlannerDao {
    /**
     * 주어진 <code>id</code>에 해당하는 플래너를 가져옵니다.
     *
     * @param id 플래너의 일련번호
     */
    public DaoResult<Planner> getPlanner(int id);

    /**
     * <code>owner</code>가 소유하고 있는 모든 플래너의 리스트를 가져옵니다.
     *
     * @param owner 소유자 객체
     */
    public DaoResult<List<Planner>> getPlanners(ItemOwner owner);

    /**
     * <code>owner</code>가 소유하게 될 새로운 플래너를 생성합니다.
     *
     * @param owner   새로운 플래너의 소유자
     * @param planner 생성할 플래너의 정보
     */
    public DaoResult<Planner> createPlanner(ItemOwner owner, Planner planner);

    /**
     * 주어진 <code>id</code>에 해당하는 플래너의 정보를 변경합니다.
     *
     * @param id      변경할 플래너의 일련번호
     * @param planner 변경할 정보가 들어 있는 {@link Planner} 객체
     */
    public DaoResult<Planner> updatePlanner(int id, Planner planner);

    /**
     * 주어진 <code>id</code>에 해당하는 플래너를 삭제합니다.
     * 
     * @param id 삭제할 플래너의 일련번호
     */
    public DaoResult<Boolean> deletePlanner(int id);
}

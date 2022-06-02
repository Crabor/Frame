package platform.service.inv.struct.inv;

public interface Inv {
    /*!
     * @brief 变量是否违反不变式
     * @param[in] var 变量
     * @return 是否违反不变式
     */
    boolean isViolated(double value);
}

package com.wangdazhi.wangaiagent.agent;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {
    /**
     * think的作用就是处理当前状态，并返回下一步的是否执行的指令
     * @return
     */
    public abstract boolean think();

    /**
     * act的作用就是执行下一步的指令
     * @return
     */
    public abstract String act();
    public abstract String present();

    @Override
    public String step() {

        try {
            if (think()) {
                 return act();
            }
            return present();
        } catch (Exception e) {
            e.printStackTrace();
            return "发生错误-结束行动"+e.getMessage();
        }
    }
}

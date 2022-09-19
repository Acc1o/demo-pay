package cn.ww.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * @author Su-wenwu
 * @createTime 2022-09-16-15:25
 */

@Data
public class BaseEntity {

    //定义主键策略：跟随数据库的主键自增
    @TableId(value = "id", type = IdType.AUTO)
    private String id; //主键

    private Date createTime;//创建时间

    private Date updateTime;//更新时间
}

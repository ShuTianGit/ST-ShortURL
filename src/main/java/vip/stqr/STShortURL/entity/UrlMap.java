package vip.stqr.STShortURL.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * url映射表
 *
 * @author 曙天
 * @since 2022-09-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UrlMap", description = "url映射表")
@TableName("url_map")
public class UrlMap {


    // @Null(groups = Create.class)
    // @NotNull(groups = Update.class)
	@Schema(description = "索引")
	private Long id;

    /**
     * 短链接
     */
    @Schema(description = "短链接标识")
    @Size(max = 2147483647)
    private String shortUrl;

    /**
     * 长链接
     */
    @Schema(description = "长链接标识")
    @Size(max = 2147483647)
    private String longUrl;

    /**
     * 访问次数
     */
    @Schema(description = "链接访问次数")
    @PositiveOrZero
    private Long accessCount;

    @Schema(description = "状态 0、禁用 1、正常")
    @PositiveOrZero
    private Integer status;

    /**
     * 创建人
     */
    protected String createBy;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@TableField(fill = FieldFill.INSERT)
    private Date createTime;


	/**
	 * 修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 删除 0、否 1、是
     */
    @JsonIgnore
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}

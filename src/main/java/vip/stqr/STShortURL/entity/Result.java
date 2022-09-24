package vip.stqr.STShortURL.entity;

import lombok.*;

/**
 * @Description: 响应结果封装
 * @Author: Naccl
 * @Date: 2021-03-21
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Result {
	private Integer code;
	private String msg;
	private Object data;

	public Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static Result ok(String msg, Object data) {
		return new Result(200, msg, data);
	}

	public static Result create(Integer code, String msg, Object data) {
		return new Result(code, msg, data);
	}

	public static Result create(Integer code, String msg) {
		return new Result(code, msg);
	}
}

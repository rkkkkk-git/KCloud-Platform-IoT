/*
 * Copyright (c) 2022-2024 KCloud-Platform-IoT Author or Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.laokou.auth.command.query;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.dto.TenantGetIDQry;
import org.laokou.auth.gatewayimpl.database.TenantMapper;
import org.laokou.auth.gatewayimpl.database.dataobject.TenantDO;
import org.laokou.auth.config.TenantProperties;
import org.laokou.common.core.utils.RegexUtil;
import org.laokou.common.core.utils.RequestUtil;
import org.laokou.common.i18n.dto.Result;
import org.laokou.common.i18n.utils.ObjectUtil;
import org.laokou.common.i18n.utils.StringUtil;
import org.laokou.common.redis.utils.RedisKeyUtil;
import org.laokou.common.redis.utils.RedisUtil;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.laokou.common.i18n.common.constants.StringConstant.BACKSLASH;
import static org.laokou.common.i18n.common.constants.StringConstant.DOT;
import static org.laokou.common.mybatisplus.mapper.BaseDO.DEFAULT_TENANT_ID;

/**
 * 根据域名查看租户ID执行器.
 *
 * @author laokou
 */
@Component
@RequiredArgsConstructor
public class TenantGetIDQryExe {

	/**
	 * www.
	 */
	private static final String WWW = "www";

	private final TenantProperties tenantProperties;

	private final TenantMapper tenantMapper;

	private final RedisUtil redisUtil;

	/**
	 * 执行根据域名查看租户ID.
	 * @param qry 查看租户ID参数
	 * @return 租户ID
	 */
	public Result<Long> execute(TenantGetIDQry qry) {
		String domainName = RequestUtil.getDomainName(qry.getRequest());
		if (StringUtil.isEmpty(domainName) || RegexUtil.ipRegex(domainName)) {
			return Result.ok(DEFAULT_TENANT_ID);
		}
		String[] split = domainName.split(BACKSLASH + DOT);
		if (split.length < 3 || WWW.equals(split[0])) {
			return Result.ok(DEFAULT_TENANT_ID);
		}
		Set<String> domainNames = tenantProperties.getDomainNames();
		// 租户域名
		if (domainNames.parallelStream().anyMatch(domainName::contains)) {
			return Result.ok(getTenantCache(split[0]));
		}
		return Result.ok(DEFAULT_TENANT_ID);
	}

	/**
	 * 根据域名从Redis获取租户ID.
	 * @param str 域名
	 * @return 租户ID
	 */
	private Long getTenantCache(String str) {
		String tenantDomainNameHashKey = RedisKeyUtil.getTenantDomainNameHashKey();
		Object o = redisUtil.hGet(tenantDomainNameHashKey, str);
		if (ObjectUtil.isNotNull(o)) {
			return Long.valueOf(o.toString());
		}
		TenantDO tenantDO = tenantMapper
			.selectOne(Wrappers.lambdaQuery(TenantDO.class).eq(TenantDO::getLabel, str).select(TenantDO::getId));
		if (ObjectUtil.isNotNull(tenantDO)) {
			Long id = tenantDO.getId();
			redisUtil.hSet(tenantDomainNameHashKey, str, id, RedisUtil.HOUR_ONE_EXPIRE);
			return id;
		}
		return DEFAULT_TENANT_ID;
	}

}

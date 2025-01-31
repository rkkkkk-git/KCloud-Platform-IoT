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

package org.laokou.admin.service;

import lombok.RequiredArgsConstructor;
import org.laokou.admin.api.IndexsServiceI;
import org.laokou.admin.command.index.query.IndexGetQryExe;
import org.laokou.admin.command.index.query.IndexListQryExe;
import org.laokou.admin.command.index.query.IndexTraceGetQryExe;
import org.laokou.admin.command.index.query.IndexTraceListQryExe;
import org.laokou.admin.dto.index.IndexGetQry;
import org.laokou.admin.dto.index.IndexListQry;
import org.laokou.admin.dto.index.IndexTraceGetQry;
import org.laokou.admin.dto.index.IndexTraceListQry;
import org.laokou.admin.dto.index.clientobject.IndexCO;
import org.laokou.common.i18n.dto.Datas;
import org.laokou.common.i18n.dto.Result;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 索引管理.
 *
 * @author laokou
 */
@Service
@RequiredArgsConstructor
public class IndexsServiceImpl implements IndexsServiceI {

	private final IndexListQryExe indexListQryExe;

	private final IndexGetQryExe indexGetQryExe;

	private final IndexTraceListQryExe indexTraceListQryExe;

	private final IndexTraceGetQryExe indexTraceGetQryExe;

	/**
	 * 查询索引列表.
	 * @param qry 查询索引列表参数
	 * @return 索引列表
	 */
	@Override
	public Result<Datas<IndexCO>> findList(IndexListQry qry) {
		return indexListQryExe.execute(qry);
	}

	/**
	 * 根据索引名称查看索引信息.
	 * @param qry 根据索引名称查看索引信息参数
	 * @return 索引信息
	 */
	@Override
	public Result<Map<String, Object>> findByIndexName(IndexGetQry qry) {
		return indexGetQryExe.execute(qry);
	}

	/**
	 * 查询分布式链路索引列表.
	 * @param qry 查询分布式链路索引列表参数
	 * @return 分布式链路索引列表
	 */
	@Override
	public Result<Datas<Map<String, Object>>> findTraceList(IndexTraceListQry qry) {
		return indexTraceListQryExe.execute(qry);
	}

	/**
	 * 根据ID查看分布式链路索引.
	 * @param qry 根据ID查看分布式链路索引参数
	 * @return 分布式链路索引
	 */
	@Override
	public Result<Map<String, Object>> findTraceById(IndexTraceGetQry qry) {
		return indexTraceGetQryExe.execute(qry);
	}

}

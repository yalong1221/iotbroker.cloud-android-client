package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.array.TLVArray;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVNull;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.variable.TLVVariable;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mobius Software LTD
 * Copyright 2015-2017, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

public class TLVFactory {

	public static TLVAmqp getTlv(ByteBuf buf) {

		SimpleConstructor constructor = getConstructor(buf);

		TLVAmqp tlv = getElement(constructor, buf);
		return tlv;
	}

	private static TLVAmqp getElement(SimpleConstructor constructor, ByteBuf buf) {

		TLVAmqp tlv = null;

		AMQPType code = constructor.getCode();
		switch (code) {

		case NULL:
			tlv = new TLVNull();
			break;

		case BOOLEAN_TRUE:
		case BOOLEAN_FALSE:
		case UINT_0:
		case ULONG_0:
			tlv = new TLVFixed(code, new byte[0]);
			break;

		case BOOLEAN:
		case UBYTE:
		case BYTE:
		case SMALL_UINT:
		case SMALL_INT:
		case SMALL_ULONG:
		case SMALL_LONG:
			byte valueOne = buf.readByte();
			tlv = new TLVFixed(code, new byte[] { valueOne });
			break;

		case SHORT:
		case USHORT:
			byte[] valueTwo = new byte[2];
			buf.readBytes(valueTwo);
			tlv = new TLVFixed(code, valueTwo);
			break;

		case UINT:
		case INT:
		case FLOAT:
		case DECIMAL_32:
		case CHAR:
			byte[] valueFour = new byte[4];
			buf.readBytes(valueFour);
			tlv = new TLVFixed(code, valueFour);
			break;

		case ULONG:
		case LONG:
		case DECIMAL_64:
		case DOUBLE:
		case TIMESTAMP:
			byte[] valueEight = new byte[8];
			buf.readBytes(valueEight);
			tlv = new TLVFixed(code, valueEight);
			break;

		case DECIMAL_128:
		case UUID:
			byte[] valueSixteen = new byte[16];
			buf.readBytes(valueSixteen);
			tlv = new TLVFixed(code, valueSixteen);
			break;

		case STRING_8:
		case SYMBOL_8:
		case BINARY_8:
			int var8length = buf.readByte() & 0xff;
			byte[] varValue8 = new byte[var8length];
			buf.readBytes(varValue8, 0, varValue8.length);
			tlv = new TLVVariable(code, varValue8);
			break;

		case STRING_32:
		case SYMBOL_32:
		case BINARY_32:
			int var32length = buf.readInt();
			byte[] varValue32 = new byte[var32length];
			buf.readBytes(varValue32, 0, varValue32.length);
			tlv = new TLVVariable(code, varValue32);
			break;

		case LIST_0:
			tlv = new TLVList();
			break;

		case LIST_8:
			int list8size = buf.readByte() & 0xff;
			int list8count = buf.readByte() & 0xff;
			List<TLVAmqp> list8values = new ArrayList<TLVAmqp>();
			for (int i = 0; i < list8count; i++)
				list8values.add(TLVFactory.getTlv(buf));
			tlv = new TLVList(code, list8values);
			break;

		case LIST_32:
			int list32size = buf.readInt();
			int list32count = buf.readInt();
			List<TLVAmqp> list32values = new ArrayList<TLVAmqp>();
			for (int i = 0; i < list32count; i++)
				list32values.add(TLVFactory.getTlv(buf));
			tlv = new TLVList(code, list32values);
			break;

		case MAP_8:
			Map<TLVAmqp, TLVAmqp> map8 = new LinkedHashMap<TLVAmqp, TLVAmqp>();
			int map8size = buf.readByte() & 0xff;
			int map8count = buf.readByte() & 0xff;
			int stop8 = buf.readerIndex() + map8size - 1;
			while (buf.readerIndex() < stop8)
				map8.put(TLVFactory.getTlv(buf), TLVFactory.getTlv(buf));
			tlv = new TLVMap(code, map8);
			break;

		case MAP_32:
			Map<TLVAmqp, TLVAmqp> map32 = new LinkedHashMap<TLVAmqp, TLVAmqp>();
			int map32size = buf.readInt();
			int map32count = buf.readInt();
			int stop32 = buf.readerIndex() + map32size - 4;
			while (buf.readerIndex() < stop32)
				map32.put(TLVFactory.getTlv(buf), TLVFactory.getTlv(buf));
			tlv = new TLVMap(code, map32);
			break;

		case ARRAY_8:
			List<TLVAmqp> arr8 = new ArrayList<TLVAmqp>();
			int array8size = buf.readByte() & 0xff;
			int array8count = buf.readByte() & 0xff;
			SimpleConstructor arr8constructor = getConstructor(buf);
			for (int i = 0; i < array8count; i++)
				arr8.add(TLVFactory.getElement(arr8constructor, buf));
			tlv = new TLVArray(code, arr8);
			break;

		case ARRAY_32:
			List<TLVAmqp> arr32 = new ArrayList<TLVAmqp>();
			int array32size = buf.readInt();
			int array32count = buf.readInt();
			SimpleConstructor arr32constructor = getConstructor(buf);
			for (int i = 0; i < array32count; i++)
				arr32.add(TLVFactory.getElement(arr32constructor, buf));
			tlv = new TLVArray(code, arr32);
			break;

		default:
			break;
		}

		if (constructor instanceof DescribedConstructor)
			tlv.setConstructor(constructor);

		return tlv;
	}

	private static SimpleConstructor getConstructor(ByteBuf buf) {
		AMQPType code = null;
		SimpleConstructor constructor = null;
		byte codeByte = buf.readByte();
		if (codeByte == 0) {
			TLVAmqp descriptor = getTlv(buf);
			code = AMQPType.valueOf(buf.readByte() & 0xff);
			constructor = new DescribedConstructor(code, descriptor);
		} else {
			code = AMQPType.valueOf(codeByte & 0xff);
			constructor = new SimpleConstructor(code);
		}
		return constructor;
	}

}

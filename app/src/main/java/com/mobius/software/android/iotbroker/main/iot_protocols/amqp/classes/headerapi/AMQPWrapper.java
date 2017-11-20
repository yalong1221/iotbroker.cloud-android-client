package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.array.TLVArray;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVNull;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.variable.TLVVariable;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPDecimal;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

public class AMQPWrapper {

	public static TLVAmqp wrap(Object o) {

		TLVAmqp result = null;

		if (o == null)
			return new TLVNull();

		if (o instanceof Byte)
			result = wrapByte((Byte) o);
		else if (o instanceof Short)
			result = (Short) o >= 0 ? wrapUByte((Short) o) : wrapShort((Short) o);
		else if (o instanceof Integer)
			result = (Integer) o >= 0 ? wrapUShort((Integer) o) : wrapInt((Integer) o);
		else if (o instanceof Long)
			result = (Long) o >= 0 ? wrapUInt((Long) o) : wrapLong((Long) o);
		else if (o instanceof BigInteger)
			result = wrapULong((BigInteger) o);
		else if (o instanceof String)
			result = wrapString((String) o);
		else if (o instanceof AMQPSymbol)
			result = wrapSymbol((AMQPSymbol) o);
		else if (o instanceof byte[])
			result = wrapBinary((byte[]) o);
		else if (o instanceof Boolean)
			result = wrapBool((Boolean) o);
		else if (o instanceof Character)
			result = wrapChar((Character) o);
		else if (o instanceof Double)
			result = wrapDouble((Double) o);
		else if (o instanceof Float)
			result = wrapFloat((Float) o);
		else if (o instanceof UUID)
			result = wrapUuid((UUID) o);
		else if (o instanceof Date)
			result = wrapTimestamp((Date) o);
		else if (o instanceof AMQPDecimal) {
			byte[] val = ((AMQPDecimal) o).getValue();
			if (val.length == 4)
				result = wrapDecimal32((AMQPDecimal) o);
			else if (val.length == 8)
				result = wrapDecimal64((AMQPDecimal) o);
			else if (val.length == 16)
				result = wrapDecimal128((AMQPDecimal) o);
		} else
			throw new IllegalArgumentException("Wrapper received unrecognized type");

		return result;
	}

	public static TLVFixed wrapBool(boolean b) {
		byte[] value = new byte[0];
		AMQPType code = b ? AMQPType.BOOLEAN_TRUE : AMQPType.BOOLEAN_FALSE;
		return new TLVFixed(code, value);
	}

	public static TLVFixed wrapUByte(short b) {
		if (b < 0)
			throw new IllegalArgumentException("negative value of " + b
					+ " cannot be assignet to UBYTE type");
		return new TLVFixed(AMQPType.UBYTE, new byte[] { (byte) b });
	}

	public static TLVFixed wrapByte(byte b) {
		return new TLVFixed(AMQPType.BYTE, new byte[] { b });
	}

	public static TLVFixed wrapUInt(long i) {
		if (i < 0)
			throw new IllegalArgumentException("negative value of " + i
					+ " cannot be assignet to UINT type");
		byte[] value = convertUInt(i);
		AMQPType code = null;
		if (value.length == 0)
			code = AMQPType.UINT_0;
		else if (value.length == 1)
			code = AMQPType.SMALL_UINT;
		else if (value.length > 1)
			code = AMQPType.UINT;

		return new TLVFixed(code, value);
	}

	public static TLVFixed wrapInt(int i) {
		byte[] value = convertInt(i);
		AMQPType code = value.length > 1 ? AMQPType.INT : AMQPType.SMALL_INT;
		return new TLVFixed(code, value);
	}

	public static TLVFixed wrapULong(BigInteger l) {
		if (l == null)
			throw new IllegalArgumentException("Wrapper cannot wrap ulong null");
		if (l.compareTo(BigInteger.ZERO) < 0)
			throw new IllegalArgumentException("negative value of " + l
					+ " cannot be assignet to ULONG type");
		byte[] value = convertULong(l);
		AMQPType code = null;
		if (value.length == 0)
			code = AMQPType.ULONG_0;
		else if (value.length == 1)
			code = AMQPType.SMALL_ULONG;
		else
			code = AMQPType.ULONG;
		return new TLVFixed(code, value);
	}

	public static TLVFixed wrapLong(long l) {
		byte[] value = convertLong(l);
		AMQPType code = value.length > 1 ? AMQPType.LONG : AMQPType.SMALL_LONG;
		return new TLVFixed(code, value);
	}

	public static TLVVariable wrapBinary(byte[] b) {
		if (b == null)
			throw new IllegalArgumentException("Wrapper cannot wrap binary null");
		AMQPType code = b.length > 255 ? AMQPType.BINARY_32 : AMQPType.BINARY_8;
		return new TLVVariable(code, b);
	}

	public static TLVFixed wrapUuid(UUID u) {
		if (u == null)
			throw new IllegalArgumentException("Wrapper cannot wrap uuid null");
		return new TLVFixed(AMQPType.UUID, u.toString().getBytes());
	}

	public static TLVFixed wrapUShort(int s) {
		if (s < 0)
			throw new IllegalArgumentException("negative value of " + s
					+ " cannot be assignet to USHORT type");
		return new TLVFixed(AMQPType.USHORT, ByteBuffer.allocate(2).putShort((short) s).array());
	}

	public static TLVFixed wrapShort(short s) {
		return new TLVFixed(AMQPType.SHORT, ByteBuffer.allocate(2).putShort(s).array());
	}

	public static TLVFixed wrapDouble(double d) {
		return new TLVFixed(AMQPType.DOUBLE, ByteBuffer.allocate(8).putDouble(d).array());
	}

	public static TLVFixed wrapFloat(float f) {
		return new TLVFixed(AMQPType.FLOAT, ByteBuffer.allocate(4).putFloat(f).array());
	}

	public static TLVFixed wrapChar(int c) {
		return new TLVFixed(AMQPType.CHAR, ByteBuffer.allocate(4).putInt(c).array());
	}

	public static TLVFixed wrapTimestamp(Date date) {
		if (date == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null Timestamp");
		return new TLVFixed(AMQPType.TIMESTAMP, ByteBuffer.allocate(8).putLong(date.getTime())
				.array());
	}

	public static TLVFixed wrapDecimal32(AMQPDecimal d) {
		if (d == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null decimal32");
		return new TLVFixed(AMQPType.DECIMAL_32, d.getValue());
	}

	public static TLVFixed wrapDecimal64(AMQPDecimal d) {
		if (d == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null decimal64");
		return new TLVFixed(AMQPType.DECIMAL_64, d.getValue());
	}

	public static TLVFixed wrapDecimal128(AMQPDecimal d) {
		if (d == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null decimal128");
		return new TLVFixed(AMQPType.DECIMAL_128, d.getValue());
	}

	public static TLVVariable wrapString(String s) {
		if (s == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null String");
		byte[] value = s.getBytes();
		AMQPType code = value.length > 255 ? AMQPType.STRING_32 : AMQPType.STRING_8;
		return new TLVVariable(code, value);
	}

	public static TLVVariable wrapSymbol(AMQPSymbol symbol) {
		if (symbol == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null symbol");
		byte[] value = symbol.getValue().getBytes(Charset.forName("US-ASCII"));
		AMQPType code = value.length > 255 ? AMQPType.SYMBOL_32 : AMQPType.SYMBOL_8;
		return new TLVVariable(code, value);
	}

	public static <T> TLVList wrapList(List<T> input) {
		if (input == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null List");
		TLVList list = new TLVList();
		for (T o : input)
			list.addElement(wrap(o));
		return list;
	}

	public static <T> TLVMap wrapMap(Map<T, Object> input) {
		if (input == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null Map");
		TLVMap map = new TLVMap();
		TLVAmqp key, value;
		for (Map.Entry<T, Object> entry : input.entrySet()) {
			key = wrap(entry.getKey());
			value = wrap(entry.getValue());
			map.putElement(key, value);
		}
		return map;
	}

	public static <T> TLVArray wrapArray(List<T> input) {
		if (input == null)
			throw new IllegalArgumentException("Wrapper cannot wrap null array");
		TLVArray array = new TLVArray();
		for (T s : input)
			array.addElement(wrap(s));
		return array;
	}

	private static byte[] convertUInt(long i) {
		if (i == 0)
			return new byte[0];
		else if (i > 0 && i <= 255)
			return new byte[] { (byte) i };
		else
			return ByteBuffer.allocate(4).putInt((int) i).array();
	}

	private static byte[] convertInt(int i) {
		if (i == 0)
			return new byte[0];
		else if (i >= -128 && i <= 127)
			return new byte[] { (byte) i };
		else
			return ByteBuffer.allocate(4).putInt(i).array();
	}

	private static byte[] convertULong(BigInteger l) {
		if (l.longValue() == 0)
			return new byte[0];
		else if (l.longValue() > 0 && l.longValue() <= 255)
			return new byte[] { (byte) l.longValue() };
		else
			return ByteBuffer.allocate(8).putLong(l.longValue()).array();
	}

	private static byte[] convertLong(long l) {
		if (l == 0)
			return new byte[0];
		else if (l >= -128 && l <= 127)
			return new byte[] { (byte) l };
		else
			return ByteBuffer.allocate(8).putLong(l).array();
	}
}

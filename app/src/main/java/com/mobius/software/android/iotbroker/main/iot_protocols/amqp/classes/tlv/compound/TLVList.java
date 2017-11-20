package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.array.TLVArray;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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

public class TLVList extends TLVAmqp {

	int width, size, count;

	protected List<TLVAmqp> list = new ArrayList<TLVAmqp>();

	public TLVList() {
		super(new SimpleConstructor(AMQPType.LIST_0));
		width = 0;
		count = 0;
		size = 0;
	}

	public TLVList(AMQPType code, List<TLVAmqp> value) {
		super(new SimpleConstructor(code));
		this.list = value;
		width = code.equals(AMQPType.LIST_8) ? 1 : 4;
		size += width;
		for (TLVAmqp tlv : value)
			size += tlv.getLength();
		count = value.size();
	}

	public void addElement(TLVAmqp element) {
		if (size == 0) {
			constructor.setCode(AMQPType.LIST_8);
			width = 1;
			size += 1;
		}
		list.add(element);
		count++;
		size += element.getLength();
		update();
	}

	public void setElement(int index, TLVAmqp element) {
		size -= list.get(index).getLength();
		list.set(index, element);
		size += element.getLength();
		update();
	}

	public void addElement(int index, TLVAmqp element) {
		int diff = index - list.size();
		do {
			addElement(new TLVNull());
		} while (diff-- > 0);
		setElement(index, element);
	}

	public void addToList(int index, int elemIndex, TLVAmqp element) {
		if (count <= index)
			addElement(index, new TLVList());
		TLVAmqp list = this.list.get(index);
		if (list.isNull())
			setElement(index, new TLVList());
		((TLVList) this.list.get(index)).addElement(elemIndex, element);
		size += element.getLength();
		update();
	}

	public void addToMap(int index, TLVAmqp key, TLVAmqp value) {
		if (count <= index)
			addElement(index, new TLVMap());
		TLVAmqp map = list.get(index);
		if (map.isNull())
			setElement(index, new TLVMap());
		((TLVMap) list.get(index)).putElement(key, value);
		size += key.getLength() + value.getLength();
		update();
	}

	public void addToArray(int index, TLVAmqp element) {
		if (count <= index)
			addElement(index, new TLVArray());
		TLVAmqp array = list.get(index);
		if (array.isNull())
			setElement(index, new TLVArray());
		((TLVArray) list.get(index)).addElement(element);
		size += element.getLength();
		update();
	}

	@Override
	public byte[] getBytes() {
		byte[] constructorBytes = constructor.getBytes();
		byte[] sizeBytes = new byte[width];
		switch (width) {
		case 0:
			break;
		case 1:
			ByteBuffer.wrap(sizeBytes).put((byte) size);
			break;
		default:
			ByteBuffer.wrap(sizeBytes).putInt(size);
			break;
		}
		byte[] countBytes = new byte[width];
		switch (width) {
		case 0:
			break;
		case 1:
			ByteBuffer.wrap(countBytes).put((byte) count);
			break;
		default:
			ByteBuffer.wrap(countBytes).putInt(count);
			break;
		}
		byte[] valueBytes = new byte[size - width];
		int pos = 0;
		byte[] tlvBytes;
		for (TLVAmqp tlv : list) {
			tlvBytes = tlv.getBytes();
			System.arraycopy(tlvBytes, 0, valueBytes, pos, tlvBytes.length);
			pos += tlvBytes.length;
		}
		byte[] bytes = new byte[constructorBytes.length + sizeBytes.length + countBytes.length
				+ valueBytes.length];
		System.arraycopy(constructorBytes, 0, bytes, 0, constructorBytes.length);
		if (size > 0) {
			System.arraycopy(sizeBytes, 0, bytes, constructorBytes.length, sizeBytes.length);
			System.arraycopy(countBytes, 0, bytes, constructorBytes.length + sizeBytes.length,
					countBytes.length);
			System.arraycopy(valueBytes, 0, bytes, constructorBytes.length + sizeBytes.length
					+ countBytes.length, valueBytes.length);
		}
		return bytes;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TLVAmqp element : list)
			sb.append(element.toString() + "\n");
		return sb.toString();
	}

	protected void update() {
		if (width == 1 && size > 255) {
			constructor.setCode(AMQPType.LIST_32);
			width = 4;
			size += 3;
		}
	}

	public List<TLVAmqp> getList() {
		return list;
	}

	@Override
	public byte[] getValue() {
		return null;
	}

	public int getLength() {
		return constructor.getLength() + width + size;
	}

}

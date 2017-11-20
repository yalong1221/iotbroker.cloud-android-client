package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.array;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;

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

public class TLVArray extends TLVAmqp {

	private List<TLVAmqp> elements = new ArrayList<TLVAmqp>();

	private int width, size, count;
	private SimpleConstructor elementConstructor;

	public TLVArray() {
		super(new SimpleConstructor(AMQPType.ARRAY_8));
		width = 1;
		size = 0;
		count = 0;
	}

	public TLVArray(AMQPType code, List<TLVAmqp> elements) {
		super(new SimpleConstructor(code));
		this.elements = elements;
		width = code.equals(AMQPType.ARRAY_8) ? 1 : 4;
		size += width;
		for (TLVAmqp element : elements) {
			size += element.getLength() - element.getConstructor().getLength();
			if (elementConstructor == null && element != null)
				elementConstructor = element.getConstructor();
		}
		size += elementConstructor.getLength();
		count = elements.size();
	}

	public SimpleConstructor getElementConstructor() {
		return elementConstructor;
	}

	public AMQPType getElemetsCode() {
		return elementConstructor.getCode();
	}

	public void addElement(TLVAmqp element) {
		if (elements.isEmpty()) {
			elementConstructor = element.getConstructor();
			size += width;
			size += elementConstructor.getLength();
		}
		elements.add(element);
		count++;
		size += element.getLength() - elementConstructor.getLength();
		if (width == 1 && size > 255) {
			constructor.setCode(AMQPType.ARRAY_32);
			width = 4;
			size += 3;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TLVAmqp element : elements)
			sb.append(element.toString() + "\n");
		return sb.toString();
	}

	@Override
	public byte[] getBytes() {
		byte[] constructorBytes = constructor.getBytes();
		byte[] sizeBytes = new byte[width];
		switch (width) {
		case 0:
			break;
		case 1:
			sizeBytes[0] = (byte) size;
			break;
		case 4:
			ByteBuffer.wrap(sizeBytes).putInt(size);
			break;
		}

		byte[] countBytes = new byte[width];
		switch (width) {
		case 0:
			break;
		case 1:
			countBytes[0] = (byte) count;
			break;
		case 4:
			ByteBuffer.wrap(countBytes).putInt(count);
			break;
		}
		byte[] elemetConstructorBytes = elementConstructor.getBytes();
		byte[] valueBytes = new byte[size - width - elemetConstructorBytes.length];
		int pos = 0;
		byte[] tlvBytes;
		for (TLVAmqp tlv : elements) {
			tlvBytes = tlv.getBytes();
			System.arraycopy(tlvBytes, elemetConstructorBytes.length, valueBytes, pos,
					tlvBytes.length - elemetConstructorBytes.length);
			pos += tlvBytes.length - elemetConstructorBytes.length;
		}

		byte[] bytes = new byte[constructorBytes.length + sizeBytes.length + countBytes.length
				+ elemetConstructorBytes.length + valueBytes.length];
		System.arraycopy(constructorBytes, 0, bytes, 0, constructorBytes.length);
		if (size > 0) {
			System.arraycopy(sizeBytes, 0, bytes, constructorBytes.length, sizeBytes.length);
			System.arraycopy(countBytes, 0, bytes, constructorBytes.length + sizeBytes.length,
					countBytes.length);
			System.arraycopy(elemetConstructorBytes, 0, bytes, constructorBytes.length
					+ sizeBytes.length + countBytes.length, elemetConstructorBytes.length);
			System.arraycopy(valueBytes, 0, bytes, constructorBytes.length + sizeBytes.length
					+ countBytes.length + elemetConstructorBytes.length, valueBytes.length);
		}

		return bytes;
	}

	public List<TLVAmqp> getElements() {
		return elements;
	}

	@Override
	public int getLength() {
		return constructor.getLength() + size + width;
	}

	@Override
	public byte[] getValue() {
		return null;
	}

	@Override
	public boolean isNull() {
		AMQPType code = constructor.getCode();
		if (code.equals(AMQPType.NULL))
			return true;
		if (code.equals(AMQPType.ARRAY_8) || code.equals(AMQPType.ARRAY_32)) {
			if (elements.size() == 0)
				return true;
		}
		return false;
	}

}

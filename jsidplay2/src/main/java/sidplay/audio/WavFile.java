package sidplay.audio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import javax.sound.sampled.LineUnavailableException;

/**
 * File based driver to create a WAV file.
 * 
 * @author Ken Händel
 * 
 */
public class WavFile implements AudioDriver {
	private static final int HEADER_LENGTH = 44;
	private static final int HEADER_OFFSET = 8;

	private ByteBuffer sampleBuffer;

	/**
	 * WAV header format.
	 * 
	 * @author Ken Händel
	 */
	static class WavHeader {
		private static final Charset US_ASCII = Charset.forName("US-ASCII");

		private int length;
		private short format;
		private short channels;
		private int sampleFreq;
		private int bytesPerSec;
		private short blockAlign;
		private short bitsPerSample;
		private int dataChunkLen;

		private byte[] getBytes() {
			final ByteBuffer b = ByteBuffer.allocate(HEADER_LENGTH);
			b.order(ByteOrder.LITTLE_ENDIAN);
			b.put("RIFF".getBytes(US_ASCII));
			b.putInt(length);
			b.put("WAVE".getBytes(US_ASCII));
			b.put("fmt ".getBytes(US_ASCII));
			b.putInt(16);
			b.putShort(format);
			b.putShort(channels);
			b.putInt(sampleFreq);
			b.putInt(bytesPerSec);
			b.putShort(blockAlign);
			b.putShort(bitsPerSample);
			b.put("data".getBytes(US_ASCII));
			b.putInt(dataChunkLen);
			return b.array();
		}
	}

	private int samplesWritten;
	private RandomAccessFile file;
	private final WavHeader wavHdr = new WavHeader();

	@Override
	public void open(final AudioConfig cfg, String recordingFilename) throws IOException, LineUnavailableException {
		final int blockAlign = Short.BYTES * cfg.getChannels();

		sampleBuffer = ByteBuffer.allocate(cfg.getChunkFrames() * blockAlign);
		sampleBuffer.order(ByteOrder.LITTLE_ENDIAN);

		wavHdr.length = HEADER_LENGTH - HEADER_OFFSET;
		wavHdr.format = 1;
		wavHdr.channels = (short) cfg.getChannels();
		wavHdr.sampleFreq = cfg.getFrameRate();
		wavHdr.bytesPerSec = cfg.getFrameRate() * blockAlign;
		wavHdr.blockAlign = (short) blockAlign;
		wavHdr.bitsPerSample = 16;

		file = new RandomAccessFile(recordingFilename + ".wav", "rw");
		file.setLength(0);
		file.write(wavHdr.getBytes());

		samplesWritten = 0;
	}

	@Override
	public void write() throws InterruptedException {
		try {
			int len = sampleBuffer.capacity();
			file.write(sampleBuffer.array(), 0, len);
			samplesWritten += len;
		} catch (final IOException e) {
			e.printStackTrace();
			throw new InterruptedException();
		}
	}

	@Override
	public void close() {
		try {
			wavHdr.length = samplesWritten + HEADER_LENGTH - HEADER_OFFSET;
			wavHdr.dataChunkLen = samplesWritten;
			file.seek(0);
			file.write(wavHdr.getBytes(), 0, HEADER_LENGTH);
			file.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuffer buffer() {
		return sampleBuffer;
	}

}

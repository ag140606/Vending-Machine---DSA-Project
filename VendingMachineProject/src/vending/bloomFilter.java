package vending;

// Counting Bloom Filter
final class bloomFilter {
    private final int size;
    private final int[] counters;
    private final int k;

    public bloomFilter(int size, int k) {
        this.size = size;
        this.counters = new int[size];
        this.k = k;
        }

    private int[] indicesFor(String s) {
        int hash1 = Math.abs(s.hashCode());
        int hash2 = Math.abs(murmurHash3(s));
        int[] idx = new int[k];
        for (int i = 0; i < k; i++) {
            long combined = (hash1 + (long) i * hash2) & 0xffffffffL;
            idx[i] = (int) (combined % size);
        }
        return idx;
    }

    public void add(String s) {
        int[] idx = indicesFor(s);
        for (int i : idx) counters[i]++;
    }

    public void remove(String s) {
        int[] idx = indicesFor(s);
        for (int i : idx) {
            if (counters[i] > 0) counters[i]--;
        }
    }

    public boolean mightContain(String s) {
        int[] idx = indicesFor(s);
        for (int i : idx) if (counters[i] == 0) return false;
        return true;
    }

    // Simple MurmurHash3 flavor (32-bit)
    private static int murmurHash3(String s) {
        byte[] data = s.getBytes();
        int length = data.length;
        int h1 = 0;
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;
        int roundedEnd = (length & 0xfffffffc);  // round down to 4 byte block

        for (int i = 0; i < roundedEnd; i += 4) {
            int k1 = ((data[i] & 0xff)) |
                    ((data[i + 1] & 0xff) << 8) |
                    ((data[i + 2] & 0xff) << 16) |
                    ((data[i + 3] & 0xff) << 24);
            k1 *= c1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= c2;
            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        int k1 = 0;
        int tail = length & 0x03;
        if (tail == 3) k1 = (data[roundedEnd + 2] & 0xff) << 16;
        if (tail >= 2) k1 |= (data[roundedEnd + 1] & 0xff) << 8;
        if (tail >= 1) {
            k1 |= (data[roundedEnd] & 0xff);
            k1 *= c1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= c2;
            h1 ^= k1;
        }

        h1 ^= length;
        h1 = fmix(h1);
        return h1;
    }

    private static int fmix(int h) {
        h ^= (h >>> 16);
        h *= 0x85ebca6b;
        h ^= (h >>> 13);
        h *= 0xc2b2ae35;
        h ^= (h >>> 16);
        return h;
    }
}

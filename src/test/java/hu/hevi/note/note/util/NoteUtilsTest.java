package hu.hevi.note.note.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NoteUtilsTest {

    private NoteUtils underTest;

    @Before
    public void setUp() {
        underTest = new NoteUtils();
    }

    @Test
    public void convertToNotes() {
    }

    @Test
    public void getTags_WithoutId_ShouldHaveSizeZero() {
        // GIVEN
        String line = "[]";

        // WHEN
        List<Integer> tags = underTest.getTags(line);

        // THEN
        assertEquals(0, tags.size());
    }

    @Test
    public void getTags_WithOneId_ShouldHaveSizeOne() {
        // GIVEN
        String line = "[15]";

        // WHEN
        List<Integer> tags = underTest.getTags(line);

        // THEN
        assertEquals(1, tags.size());
    }

    @Test
    public void getTags_WithOneId_ShouldReturnCorrectValue() {
        // GIVEN
        String line = "[15]";

        // WHEN
        List<Integer> tags = underTest.getTags(line);

        // THEN
        assertEquals((Integer) 15, tags.get(0));
    }

    @Test
    public void asString() {
    }
}
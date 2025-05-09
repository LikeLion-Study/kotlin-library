package library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

internal class ApplicationTest {
    private val originalIn = System.`in`
    private val originalOut = System.out
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream, true, StandardCharsets.UTF_8.name()))
    }

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `도서 조회 기능 테스트`() {
        val input = "조회 Do it\nN\n"
        System.setIn(ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8)))

        main()

        val output = outputStream.toString(StandardCharsets.UTF_8.name())
        assertThat(output).contains("총 2권의 도서가 검색되었습니다.")
        assertThat(output).contains("[ISBN: 987-89A02001]", "\"Do it! 자료구조\"")
        assertThat(output).contains("[ISBN: 987-89A02008]", "\"Do it! 알고리즘\"")
    }

    @Test
    fun `대출 기능 오류 테스트`() {
        val input = "대출 987-89A02035\nN\n"
        System.setIn(ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8)))

        main()

        val output = outputStream.toString(StandardCharsets.UTF_8.name())
        assertThat(output).contains("[ERROR]").contains("이미 대여 중입니다.")
    }

    @Test
    fun `대출 기능 정상 테스트`() {
        val input = "대출 Do it! 알고리즘\nY\nN\n"
        System.setIn(ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8)))

        main()

        val output = outputStream.toString(StandardCharsets.UTF_8.name())
        assertThat(output).contains("[INFO] \"Do it! 알고리즘\" 대출이 완료되었습니다.")
        assertThat(output).contains("- 대출일:")
        assertThat(output).contains("- 반납일:")
    }

    @Test
    fun `반납 기능 정상 테스트`() {
        val input = "반납 987-89A01066\nN\n"
        System.setIn(ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8)))

        main()

        val output = outputStream.toString(StandardCharsets.UTF_8.name())
        assertThat(output).contains("[INFO] 검색 결과")
        assertThat(output).contains("대출일:")
        assertThat(output).contains("반납일:")
        assertThat(output).contains("연체료:")
    }

    @Test
    fun `반납 기능 오류 테스트`() {
        val input = "반납 987-89A01005\nN\n"
        System.setIn(ByteArrayInputStream(input.toByteArray(StandardCharsets.UTF_8)))

        main()

        val output = outputStream.toString(StandardCharsets.UTF_8.name())
        assertThat(output).contains("[ERROR]").contains("대여 기록이 없습니다")
    }
}
package ui

import client.exportFilePath
import client.isSeparateFilesByPlaylist
import client.saveAs
import client.saveWithName
import exporter.FileExporter.removeWindowsInvalidCharacters
import model.Playlist
import server.Server
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JLabel
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.lang.Exception
import kotlin.system.exitProcess

object UI {
    private const val screenTitle = "PlaylistTransporter"
    private const val version = "v2.0"
    private var isAlreadyStarted: Boolean = false

    private lateinit var screen: JFrame
    private lateinit var currentOperation: String
    private val label = JLabel()

    fun createLoginScreen() {
        if (!isAlreadyStarted) {
            isAlreadyStarted = true
        } else {
            screen.dispose()
        }

        val loginMessage = "Logue para $currentOperation as playlists."
        val closeMessage = "Essa mensagem se fechará automaticamente ao logar, ou você pode encerrar manualmente o app ao clicar em encerrar."

        val frame = JFrame("$screenTitle-$version").also { it.setIconImage() }
        val button = JButton("Encerrar")
        val panel = JPanel()
        val textPanel = JPanel()
        val pane = frame.contentPane

        frame.layout = null
        pane.layout = null

        frame.setSize(400, 300)
        frame.setLocationRelativeTo(null)

        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                Server.shutDown()
            }
        })

        button.addMouseListener(object : MouseListener {
            override fun mousePressed(e: MouseEvent?) {
                exitProcess(0)
            }

            override fun mouseClicked(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })

        pane.add(panel)
        pane.add(textPanel)

        panel.add(button)
        label.text = htmlCenter("$loginMessage <br><br> $closeMessage")
        textPanel.add(label)

        val insets = pane.insets
        val size = panel.preferredSize

        panel.setBounds(
            150 + insets.left,
            180 + insets.top,
            size.width,
            size.height
        )

        textPanel.setBounds(
            50,
            50,
            300,
            500
        )

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.isAlwaysOnTop = true

        screen = frame
    }

    fun createImportingScreen() {
        screen.dispose()

        val importingMessage = "$currentOperation..."

        val frame = JFrame("$screenTitle-$version").also { it.setIconImage() }
        val textPanel = JPanel()
        val pane = frame.contentPane

        frame.layout = null
        pane.layout = null

        frame.setSize(400, 300)
        frame.setLocationRelativeTo(null)

        pane.add(textPanel)
        label.text = htmlCenter(importingMessage)
        textPanel.add(label)

        textPanel.setBounds(
            0,
            90,
            400,
            300
        )

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.isAlwaysOnTop = true

        screen = frame
    }

    fun createDoneExportToFileScreen() {
        screen.dispose()

        val frame = JFrame("$screenTitle-$version").also { it.setIconImage() }
        val textPanel = JPanel()
        val pane = frame.contentPane

        frame.layout = null
        pane.layout = null

        label.text = htmlCenter("Importação concluída. <br><br>" + getFilesPath(), 700)

        val filesQuantity = label.text.windowed(4) { if (it == "<br>") 1 else 0 }.sum() - 2
        val pathLength = File(exportFilePath).listFiles()?.filter { it.extension == saveAs }?.maxByOrNull { it.name.length }?.canonicalPath?.length
            ?: 70

        frame.setSize((pathLength * 7.25).toInt(), 300 + (filesQuantity * if (filesQuantity <= 15) 3 else 11))
        frame.setLocationRelativeTo(null)

        pane.add(textPanel)
        textPanel.add(label)

        textPanel.setBounds(
            0,
            (65 - (filesQuantity * 4)).let { if (it < 4) 4 else it },
            -15 + frame.size.width,
            100 + frame.size.height
        )

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.isAlwaysOnTop = true

        screen = frame
    }

    fun createDoneExportPlaylistScreen(exportedPlaylists: List<Playlist>) {
        screen.dispose()

        val frame = JFrame("$screenTitle-$version").also { it.setIconImage() }
        val textPanel = JPanel()
        val pane = frame.contentPane

        frame.layout = null
        pane.layout = null

        val playlistsMessage = exportedPlaylists.map { "${it.title} <br> " }.reduce { acc, s -> "$acc$s" }.trim()
        label.text = htmlCenter("Exportação concluída <br>Playlists: <br><br>$playlistsMessage", 700)

        val playlistsQuantity = exportedPlaylists.size
        val pathLength = exportedPlaylists.maxByOrNull { it.title.length }?.title?.length.let { if (it == null || it < 50) 50 else it }

        frame.setSize((pathLength * 7.25).toInt(), 300 + (playlistsQuantity * if (playlistsQuantity <= 15) 3 else 11))
        frame.setLocationRelativeTo(null)

        pane.add(textPanel)
        textPanel.add(label)

        textPanel.setBounds(
            0,
            (65 - (playlistsQuantity * 4)).let { if (it < 4) 4 else it },
            -15 + frame.size.width,
            100 + frame.size.height
        )

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.isAlwaysOnTop = true

        screen = frame
    }

    fun createErrorScreen(message: String) {
        if (label.text == htmlCenter("Importando...")) screen.dispose()

        val frame = JFrame("$screenTitle-$version").also { it.setIconImage() }
        val textPanel = JPanel()
        val pane = frame.contentPane

        frame.layout = null
        pane.layout = null

        frame.setSize((message.length * 7.25).toInt().let { if (it < 350) 350 else it }, 300)
        frame.setLocationRelativeTo(null)

        pane.add(textPanel)
        label.text = htmlCenter(message, 400)
        textPanel.add(label)

        textPanel.setBounds(
            0,
            100,
            -15 + frame.size.width,
            300
        )

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.isAlwaysOnTop = true

        screen = frame
    }

    fun updateMessage(message: String) {
        label.text = htmlCenter(message)
    }

    fun updateOperation(message: String) {
        currentOperation = message
    }

    /**
     * Adds dynamic dots on the end of the message to represent search
     */
    fun addSearching() {
        val currentText = label.text

        val messageLastIndex = currentText.indexOf("</div>")
        val messageFirstIndex = currentText.indexOf("align='center'") + 16

        val currentMessage = currentText.substring(messageFirstIndex, messageLastIndex)
        val isToReset = run {
            val hasOneDot = currentText[messageLastIndex - 3] == '.'
            val hasSecondDot = currentText[messageLastIndex - 2] == '.'
            val hasThirdDot = currentText[messageLastIndex - 1] == '.'

            hasOneDot && hasSecondDot && hasThirdDot
        }

        if (isToReset) {
            updateMessage(currentMessage.substring(0, currentMessage.length - 3))
        } else {
            updateMessage("$currentMessage.")
        }
    }

    fun close() {
        screen.dispose()
    }

    private fun getFilesPath(): String {
        val files = File(exportFilePath).listFiles()?.filter {
            if (isSeparateFilesByPlaylist) {
                val createdPlaylists = Playlist.createdPlaylists.map { playlist -> playlist.title.removeWindowsInvalidCharacters() }
                createdPlaylists.any { playlistTitle -> it.name.startsWith(playlistTitle) }
            } else {
                it.name.startsWith(saveWithName) && it.extension == saveAs
            }
        } ?: return "Não foi possível encontrar os arquivos gerados."

        val filesPath = files.map { "${it.canonicalPath} <br> " }
        return filesPath.reduce { acc, s -> "$acc$s" }.trim()
    }

    private fun htmlCenter(message: String, width: Int = 200) = "<html><body><div width='${width}px' align='center' >$message</div></body></html>"

    private fun JFrame.setIconImage() {
        try {
            val imageFile = this@UI.javaClass.classLoader.getResource("playlistTransporterLogo.png")
            val image: BufferedImage = ImageIO.read(imageFile)
            this.iconImage = image
        } catch (e: Exception) {}
    }
}
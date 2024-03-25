package red.razvan.mindthegap.ui

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.ui.home.HomeFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.system.exitProcess

class JBar(position: Int, frm: JFrame) : JMenuBar(), ActionListener {
    private val icon: Image
    private val frm: JFrame
    private val col = Color(57, 33, 9)

    init {
        val optionsMenu = JMenu("Opzioni")
        optionsMenu.mnemonic = KeyEvent.VK_O

        add(optionsMenu)
        this.icon = AppIcon
        this.frm = frm

        val restartItem = JMenuItem("Riavvia", KeyEvent.VK_R)
        restartItem.addActionListener(this)
        optionsMenu.add(restartItem)
        if (position == 0) restartItem.isEnabled = false

        optionsMenu.addSeparator()

        val exitItem = JMenuItem("Esci", KeyEvent.VK_E)
        exitItem.addActionListener(this)
        optionsMenu.add(exitItem)

        val helpMenu = JMenu("Aiuto")
        helpMenu.mnemonic = KeyEvent.VK_A
        this.add(helpMenu)

        helpMenu.addSeparator()

        val infoItem = JMenuItem("Informazioni su...", KeyEvent.VK_I)
        infoItem.addActionListener(this)
        helpMenu.add(infoItem)
    }


    override fun actionPerformed(e: ActionEvent) {
        val source = e.source as JMenuItem
        val s = source.text

        if (s == "Riavvia") {
            val n = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler riavviare il programma?",
                "Attenzione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            if (n == JOptionPane.YES_OPTION) {
                val wP = HomeFrame()
                wP.isVisible = true
                frm.isVisible = false
            }
        }

        if (s == "Informazioni su...") {
            val frm = JFrame("About")
            val pnl = JPanel()
            val frs = JPanel()
            val img = ImageIcon("img/logo.png")
            val lblInfo = arrayOf(
                JLabel("", JLabel.CENTER),
                JLabel("", JLabel.CENTER),
                JLabel(Constants.APP_NAME + " - Build 4", JLabel.CENTER),
                JLabel("Răzvan Roşu", JLabel.CENTER),
                JLabel("", JLabel.CENTER),
                JLabel("2024 - All rights reserved.", JLabel.CENTER),
                JLabel("", JLabel.CENTER),
                JLabel("", JLabel.CENTER)
            )
            val lblLogo = JLabel(img, JLabel.CENTER)
            val f = Font("Arial", Font.BOLD, 10)
            frs.layout = GridLayout(8, 1)

            for (i in 0..7) lblInfo[i].font = f

            lblLogo.horizontalTextPosition = JLabel.CENTER

            pnl.layout = BorderLayout()
            pnl.add(lblLogo, "North")

            for (i in 0..7) {
                lblInfo[i].foreground = Color.white
                frs.add(lblInfo[i])
            }

            pnl.add(frs, "Center")

            val c = frm.contentPane
            c.add(pnl)
            pnl.background = col
            frs.background = col
            frm.pack()
            frm.iconImage = icon
            frm.setLocationRelativeTo(null)
            frm.isResizable = false
            frm.isVisible = true
        }

        if (s == "Esci") {
            val t = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuor di voler uscire?",
                "Uscita",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            if (t == JOptionPane.YES_OPTION) exitProcess(0)
        }
    }
}
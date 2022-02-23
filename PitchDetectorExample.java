
// This was copied from TarsosDSP by Joren Six and modified by Selen Serdar.


import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.awt.FlowLayout;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.Color;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class PitchDetectorExample extends JFrame implements PitchDetectionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3501426880288136245L;
	private List<Note> melody;
	private final JTextArea textArea;
	private JPanel currentpage;
	private Matcher matcher;
	private List<Page> pages;
	private AudioDispatcher dispatcher;
	private JPanel rightPanel;
	private Mixer currentMixer;
	private int windowsize = 10;
	private int pagenum;
	private Listened listened;
	private PitchEstimationAlgorithm algo;
	private ActionListener algoChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			String name = e.getActionCommand();
			PitchEstimationAlgorithm newAlgo = PitchEstimationAlgorithm.valueOf(name);
			algo = newAlgo;
			try {
				setNewMixer(currentMixer);
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			}
		}
	};

	public PitchDetectorExample(Reader input) throws IOException {
		this.setLayout(new GridLayout(1, 0));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("PAGE TURNER");
		matcher = new EvenBetterMatcher();
		listened = new Listened(windowsize);

		JPanel leftPanel = new JPanel();
		rightPanel = new JPanel();

	
		JPanel big = new JPanel();
		big.setLayout(new GridLayout(0,1));

		leftPanel.setLayout(new GridLayout(0, 1));
		rightPanel.setLayout(new GridLayout(0, 1));
		add(leftPanel);
		add(rightPanel);
		add(big);
		
		
		JPanel inputPanel = new InputPanel();
		leftPanel.add(inputPanel);
		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						try {
							setNewMixer((Mixer) arg0.getNewValue());
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		algo = PitchEstimationAlgorithm.YIN;

		JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);

		leftPanel.add(pitchDetectionPanel);

		textArea = new JTextArea();
		textArea.setEditable(false);
		leftPanel.add(new JScrollPane(textArea));

		BoxLayout asdfg = new BoxLayout(big, BoxLayout.Y_AXIS);
		big.setLayout(asdfg);
		big.setBorder(new EmptyBorder(new Insets(70,0,0,0)));
		pages= new ArrayList<Page>();
		//big.add()

		
		melody = Note.parse(input, pages);
		for (int index =0 ; index< melody.size() ;index++) {
			Note note = melody.get(index);
			Page page = Page.findPage(pages, index);
			note.label = new JLabel (note.toString());
			note.label.setOpaque(true);
			page.notepanel.add(note.label);
			note.label.setBackground(Color.white);
			if (page.plable==null){
				
				page.plable= new JLabel (String.format("page %d" ,page.number+1));
				
				page.numbers.add(page.plable);
			}

		}

		currentpage=pages.get(0).panel;
		rightPanel.add(currentpage);
		

	}

	private void setNewMixer(Mixer mixer) throws LineUnavailableException,
			UnsupportedAudioFileException {

		if (dispatcher != null) {
			dispatcher.stop();
		}
		currentMixer = mixer;

		float sampleRate = 44100;
		int bufferSize = 1024;
		int overlap = 0;

		textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
				true);
		final DataLine.Info dataLineInfo = new DataLine.Info(
				TargetDataLine.class, format);
		TargetDataLine line;
		line = (TargetDataLine) mixer.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;
		line.open(format, numberOfSamples);
		line.start();
		final AudioInputStream stream = new AudioInputStream(line);

		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		// create a new dispatcher
		dispatcher = new AudioDispatcher(audioStream, bufferSize,
				overlap);

		// add a processor
		dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));

		new Thread(dispatcher, "Audio dispatching").start();
	}

	public static void main(String... strings) throws InterruptedException,
			InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					// ignore failure to set default look en feel;
				}
				try (Reader input = new FileReader("PRELUDE1")) {
					JFrame frame = new PitchDetectorExample(input);
					frame.pack();
					frame.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void highlighter(int index)  {
		for (int index2=0;index2< melody.size(); index2++){
			if (index2==index){
				melody.get(index2).label.setBackground(Color.green);
				Page page = Page.findPage(pages, index2);

				if (page.panel!= currentpage){
					rightPanel.remove(currentpage);
					rightPanel.add (page.panel);
					rightPanel.revalidate();
					rightPanel.repaint();
					currentpage=page.panel;
					
				}
			}
			else{
				melody.get(index2).label.setBackground(Color.white);
			}
		}
	}

	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {

		if (pitchDetectionResult.getPitch() != -1) {
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch();
			// float probability = pitchDetectionResult.getProbability();
			// double rms = audioEvent.getRMS() * 100;
			// WHY .018 as DELTA? The notes with the 2 largest frequencies differ in
			// frequency by
			// ~443 - the median between frequencies is then ~221.5, which is ~2.97% of
			// 0.0297 of the second largest number.
			// This threshold is too large for notes with smaller frequencies as the
			// differences get smaller.
			// 0.018 or 1.8% works for the smallest frequencies.
			double delta = 0.018 * pitch;
			String notename = Notes.getName(pitch, delta);
			if (notename != "") {
				Sound lastnote = listened.getlast();
				if (lastnote == null) {
					listened.newsound(new Sound(notename, timeStamp, 0));
				} else {
					if (lastnote.notename .equals( notename)) {
						lastnote.duration = timeStamp - lastnote.timeStamp;
					} else {
						lastnote.duration = timeStamp - lastnote.timeStamp;
						textArea.append(listened.toString() + "\n");
						textArea.setCaretPosition(textArea.getDocument().getLength());
						int locationindex = matcher.locator(melody, listened.sounds);
						if (locationindex != -1) {
								highlighter(locationindex);

						}
						listened.newsound(new Sound(notename, timeStamp, 0));
						// returns -1 for no match

					}
				}

			}
			// String message = String.format("Pitch detected at %.2fs: %.2fHz %s ( %.2f
			// probability, RMS: %.5f )\n", timeStamp,pitch,notename,probability,rms);
			// textArea.append(message);
			// textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}
}

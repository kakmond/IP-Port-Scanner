package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.NetworkScanner;

public class SampleController {
	@FXML
	private Button Play;
	@FXML
	private ListView<Integer> view2;
	@FXML
	private Button Stop;
	private Task<Void> scan_thread;
	private NetworkScanner scanner;
	@FXML
	private Button Help;
	@FXML
	private Button dnsBt;
	@FXML
	private Text status;
	@FXML
	private ProgressBar bar;
	@FXML
	private TextField text;
	@FXML
	private TextField inputPort;
	@FXML
	private Button credit;
	private Stage creditStage = new Stage();

	@FXML
	private TableView<DisplayResult> tableViewLeft;

	@FXML
	private TableView<String> tableViewRight;

	public void showCredit(ActionEvent e) {
		try {
			URL url = getClass().getResource("Credit.fxml");
			if (url == null) {
				System.out.println("Couldn't find file: Credit.fxml");
				Platform.exit();
			}
			FXMLLoader loader = new FXMLLoader(url);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			creditStage.setScene(scene);
			creditStage.sizeToScene();
			creditStage.setResizable(false);
			creditStage.setTitle("Credit By");
			creditStage.show();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
	}

	@FXML
	public void initialize() {
		Play.setDisable(false);
		Stop.setDisable(true);
	}

	public void handleMouseClick(MouseEvent arg0) {
		tableViewRight.getItems().clear();
		ObservableList<String> list = FXCollections.observableArrayList();
		DisplayResult displayPort = tableViewLeft.getSelectionModel().getSelectedItem();
		if (displayPort != null) {
			System.out.println(tableViewLeft.getSelectionModel().getSelectedItem().getPing());
			TableColumn<String, String> port = new TableColumn<>("Port");
			for (Integer s : tableViewLeft.getSelectionModel().getSelectedItem().getPort()) {
				list.add(new String(s + ""));
			}
			System.out.println(list.toString());
			port.setMinWidth(250);
			port.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
			tableViewRight.getColumns().clear();

			tableViewRight.setItems(list);
			tableViewRight.getColumns().addAll(port);

		}

	}

	public void play(ActionEvent e) {
		tableViewLeft.getItems().clear();
		tableViewRight.getItems().clear();
		// Scan IP from input
		System.out.println("Receive input ip => " + text.getText());
		String[] inputIP = text.getText().split("-");

		// Scan Port from input
		System.out.println("Receive input port => " + inputPort.getText());
		String[] inputPorts = inputPort.getText().split("-");

		// Implementation.
		// 1 IP.
		if (inputIP.length == 1) {
			String focusIP = inputIP[0];
			if (isIPv4(focusIP)) {
				System.out.println("Input IP has right pattern 1.");
				// 1 Port
				if (inputPorts.length == 1) {
					int focusPort = Integer.parseInt(inputPorts[0]);
					// Scan only 1 IP with 1 Port
					scan_thread = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							scanner.scan(focusIP, focusIP, focusPort, focusPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();
				}
				// 2 Ports
				else if (inputPorts.length == 2) {
					int startPort = Integer.parseInt(inputPorts[0]);
					int endPort = Integer.parseInt(inputPorts[1]);
					// Scan only 1 IP between 2 Ports
					scan_thread = new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							// scanner.scan(inputIP[0], inputIP[0], 0, 100);
							scanner.scan(focusIP, focusIP, startPort, endPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();
				} else {
					System.out.println("Input Port has wrong pattern, try again.");
				}
			} else {
				System.out.println("Input DomainName has right pattern.");
				// 1 Port
				if (inputPorts.length == 1) {
					int focusPort = Integer.parseInt(inputPorts[0]);
					// Scan only 1 IP with 1 Port
					scan_thread = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							scanner.scanByDomainName(focusIP, focusPort, focusPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();

				}
				// 2 Ports
				else if (inputPorts.length == 2) {
					int startPort = Integer.parseInt(inputPorts[0]);
					int endPort = Integer.parseInt(inputPorts[1]);
					// Scan only 1 IP between 2 Ports
					scan_thread = new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							// scanner.scan(inputIP[0], inputIP[0], 0, 100);
							scanner.scanByDomainName(focusIP, startPort, endPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();
				} else {
					System.out.println("Input Port has wrong pattern, try again.");
				}
			}
		}
		// 2 IP.
		else if (inputIP.length == 2) {
			boolean shouldDo = true;
			String startIP = inputIP[0];
			String endIP = inputIP[1];
			for (int i = 0; i < inputIP.length; i++) {
				if (!isIPv4(inputIP[i])) {
					shouldDo = false;
				}
			}
			if (shouldDo) {
				// 1 Port
				if (inputPorts.length == 1) {
					int focusPort = Integer.parseInt(inputPorts[0]);
					// Scan 2 IP in 1 Port
					scan_thread = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							// scanner.scan(inputIP[0], inputIP[1], 0, 100);
							scanner.scan(startIP, endIP, focusPort, focusPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();
				}
				// 2 Port
				else if (inputPorts.length == 2) {
					int startPort = Integer.parseInt(inputPorts[0]);
					int endPort = Integer.parseInt(inputPorts[1]);
					// Scan 2 IP between 2 Ports
					scan_thread = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							// scanner.scan(inputIP[0], inputIP[1], 0, 100);
							System.out.println("CALL!");
							scanner.scan(startIP, endIP, startPort, endPort);
							return null;
						}
					};
					// bar.progressProperty().bind(scan_thread.progressProperty());
					new Thread(scan_thread).start();
				} else {
					System.out.println("Input Port has wrong pattern, try again.");
				}
			} else {
				System.out.println("Some input IP has wrong pattern, try again.");
			}
		} else {
			System.out.println("Input IP is error, try again.");
			text.setText("");
		}
		Play.setDisable(true);
		Stop.setDisable(false);

	}

	public void setNetworkScanner(NetworkScanner scanner) {
		this.scanner = scanner;
	}

	@SuppressWarnings("unchecked")
	public void show(NetworkObserver obs) {
		if (obs.getList() != null) {
			obs.setController(this);
			TableColumn<DisplayResult, String> ping = new TableColumn<>("Ping");
			ping.setMinWidth(200);
			ping.setCellValueFactory(new PropertyValueFactory<DisplayResult, String>("ping"));

			TableColumn<DisplayResult, String> ip = new TableColumn<>("IP Address");
			ip.setMinWidth(200);
			ip.setCellValueFactory(new PropertyValueFactory<DisplayResult, String>("ipaddr"));

			tableViewLeft.setItems(obs.getList());
			tableViewLeft.getColumns().addAll(ping, ip);
		}

	}

	public void stop(ActionEvent e) {
		Play.setDisable(false);
		Stop.setDisable(true);
		scanner.stop();
		System.out.println("Stop");
	}

	TableView<WellPort> tViewWellPort;

	public static class WellPort {
		private String port;
		private String service;

		private WellPort(String sName, String pName) {
			this.service = sName;
			this.port = pName;
		}

		public String getPort() {
			return port;
		}

		public String getService() {
			return service;
		}

		public void setPort(String pName) {
			this.port = pName;
		}

		public void setService(String sName) {
			this.service = sName;
		}
	}

	public ObservableList<WellPort> getWellPort() {
		ObservableList<WellPort> wPorts = FXCollections.observableArrayList();
		wPorts.add(new WellPort("HTTP", "80"));
		wPorts.add(new WellPort("HTTPS", "443"));
		wPorts.add(new WellPort("FTP", "20,21"));
		wPorts.add(new WellPort("DNS", "53"));
		wPorts.add(new WellPort("SMTP", "25"));
		wPorts.add(new WellPort("POP3", "110"));
		wPorts.add(new WellPort("IMAP", "143"));
		wPorts.add(new WellPort("Telnet", "23"));
		wPorts.add(new WellPort("SSH", "22"));
		return wPorts;
	}

	private Stage priStage = new Stage();

	public void help(ActionEvent e) {
		priStage.setTitle("Port Information");

		TableColumn<WellPort, String> portName = new TableColumn<>("Port");
		portName.setMinWidth(200);
		portName.setCellValueFactory(new PropertyValueFactory<>("port"));

		TableColumn<WellPort, String> serviceName = new TableColumn<>("Service");
		serviceName.setMinWidth(200);
		serviceName.setCellValueFactory(new PropertyValueFactory<>("service"));
		tViewWellPort = new TableView<>();
		tViewWellPort.setItems(getWellPort());
		tViewWellPort.getColumns().addAll(serviceName, portName);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(tViewWellPort);
		Scene scene = new Scene(vBox, 400, 270);
		priStage.setScene(scene);
		priStage.setResizable(false);
		priStage.show();
	}

	Button convertBt = new Button();

	Stage stageConverter;

	public void dns(ActionEvent e) {
		try {
			stageConverter = new Stage();
			URL url = getClass().getResource("Converter.fxml");
			if (url == null) {
				System.out.println("Couldn't find file: Converter.fxml");
				Platform.exit();
			}
			FXMLLoader loader = new FXMLLoader(url);
			Parent root = loader.load();

			Scene scene = new Scene(root);
			stageConverter.setScene(scene);
			stageConverter.sizeToScene();
			stageConverter.setResizable(false);
			stageConverter.setTitle("DNS lookup");
			stageConverter.show();
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
	}

	public void setStatus(String txt) {
		status.setText(txt);
	}

	public void finished() {
		Play.setDisable(false);
		Stop.setDisable(true);
		text.setEditable(true);
		inputPort.setEditable(true);
	}

	private static boolean isIPv4(final String ip) {
		String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
		return ip.matches(PATTERN);
	}
}
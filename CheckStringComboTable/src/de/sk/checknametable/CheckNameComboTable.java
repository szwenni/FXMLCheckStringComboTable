package de.sk.checknametable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class CheckNameComboTable<T, S> extends AnchorPane {

	@FXML
	private TableView<T> table;

	@FXML
	private TableColumn<T, String> name;

	@FXML
	private TableColumn<T, ComboBox<S>> validUntil;

	@FXML
	private TableColumn<T, Boolean> active;

	ObservableList<T> tableItems;
	ObservableList<S> comboItems;

	HashMap<T, BooleanProperty> selectionMapping = new HashMap<>();
	HashMap<T, S> comboMapping = new HashMap<>();

	public CheckNameComboTable() throws IOException {
		final FXMLLoader loader = new FXMLLoader();
		loader.setRoot(this);
		loader.setController(this);
		loader.setLocation(this.getClass().getResource("CheckStringComboTable_View.fxml"));

		loader.load();

	}

	public void init(ObservableList<T> tableItems, ObservableList<S> comboItems, ObservableList<T> selectedItems,
			HashMap<T, S> selectedCombos, String checkBoxName, String stringName, String comboBoxName) {
		this.tableItems = tableItems;
		this.comboItems = comboItems;
		this.comboMapping = selectedCombos;
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		validUntil.setCellFactory(new Callback<TableColumn<T, ComboBox<S>>, TableCell<T, ComboBox<S>>>() {

			@Override
			public TableCell<T, ComboBox<S>> call(TableColumn<T, ComboBox<S>> param) {
				ComboBox<S> cb = new ComboBox<>();
				cb.setItems(comboItems);
				TableCell<T, ComboBox<S>> cell = new TableCell<>();
				cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(cb));
				return cell;
			}
		});

		active.setCellFactory(
				CheckBoxTableCell.<T, Boolean>forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(Integer param) {
						return selectionMapping.get(active.getTableView().getItems().get(param));

					}
				}));
		table.setItems(tableItems);
		for (T item : tableItems) {
			if (comboMapping.containsKey(item)) {
				validUntil.getCellData(item).getSelectionModel().select(comboMapping.get(item));
			}
			validUntil.getCellData(item).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					event.consume();
					S selected = ((ComboBox<S>) event.getSource()).getSelectionModel().getSelectedItem();
					comboMapping.put(item, selected);
				}
			});
		}
	}

	public List<T> getSelectedItems() {
		Set<Entry<T, BooleanProperty>> entries = selectionMapping.entrySet();
		entries.removeIf((entry) -> !entry.getValue().get());
		List<T> ret = new ArrayList<>();
		entries.forEach((entry) -> ret.add(entry.getKey()));
		return ret;
	}

	public S getComboValueForItem(T item) {
		return validUntil.getCellData(item).getSelectionModel().getSelectedItem();
	}
}

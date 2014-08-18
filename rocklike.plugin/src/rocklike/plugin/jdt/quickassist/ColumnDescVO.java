package rocklike.plugin.jdt.quickassist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="desc")
public class ColumnDescVO implements Serializable{

	@XmlElement(name="column")
	private List<Column> columns = new ArrayList();

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public ColumnDescVO addColumn(Column column){
		columns.add(column);
		return this;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement
	public static class Column implements Serializable{
		@XmlAttribute
		public String name;
		@XmlValue
		public String description;
		public Column(){}
		public Column(String name, String description) {
	        super();
	        this.name = name;
	        this.description = description;
        }
	}


	public static void main(String[] args) throws JAXBException, FileNotFoundException, IOException, ClassNotFoundException {
	    JAXBContext ctx = JAXBContext.newInstance(ColumnDescVO.class);
	    Marshaller m = ctx.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

	    ColumnDescVO v = new ColumnDescVO();
	    v.addColumn(new Column("CONTS_BUY_AFTR_USE_TIME", "콘텐츠구매후사용시간"));
	    v.addColumn(new Column("EMG_YN", "긴급여부"));
	    v.addColumn(new Column("CONTS_BUY_AFTR_USE_TIME", "콘텐츠구매후사용시간"));
	    v.addColumn(new Column("EMG_YN", "긴급여부"));

	    StringWriter sw = new StringWriter();
	    m.marshal(v, sw);

	    System.out.println(sw);
	    System.out.println("=========================");
	    long start = System.currentTimeMillis();
	    long end = 0;
	    Unmarshaller um = ctx.createUnmarshaller();
	    ColumnDescVO vo = (ColumnDescVO) um.unmarshal(new File("D:/eclipse/eclipse-workspace/luna/modeling/hong.pluginTest/columnDesc.xml"));

	    end = System.currentTimeMillis();
	    System.out.printf("== marshaling에 걸린시간 : %s \n", (end - start));
	    start = System.currentTimeMillis();

	    // 객체 저장
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:/eclipse/eclipse-workspace/luna/modeling/hong.pluginTest/columnDesc.bin"));
	    oos.writeObject(vo);
	    oos.close();

	    end = System.currentTimeMillis();
	    System.out.printf("== ObjectOutputStream write에 걸린시간 : %s \n", (end - start));
	    start = System.currentTimeMillis();

	    // 객체에서 다시 읽어오기
	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D:/eclipse/eclipse-workspace/luna/modeling/hong.pluginTest/columnDesc.bin"));
	    ColumnDescVO readVo = (ColumnDescVO)ois.readObject();
	    ois.close();

	    end = System.currentTimeMillis();
	    System.out.printf("== ObjectOutputStream read에 걸린시간 : %s \n", (end - start));
	    start = System.currentTimeMillis();


    }
}




package by.grodno.zagart.observer.localapp.interfaces;

import java.io.IOException;
import java.util.List;

/**
 * ������������� ����� ���������� ����� ����� ��� ��������� ������ �� ������
 * �������� ������������ ��������. ������ ������ ���� ������������ � ����
 * ��������� List<Integer>.
 */
public interface Protocol {

    int getDataSize();

    boolean process(List<Integer> data) throws IOException;

}

package by.grodno.zagart.observer.localapp.interfaces;

import java.util.List;

/**
 * ������������� ����� ���������� ����� ����� ��� ��������� ������ �� ������
 * �������� ������������ ��������. ������ ������ ���� ������������ � ����
 * ��������� List<Integer>.
 */
public interface Protocol {

    int getDataSize();

    void process(List<Integer> data);

}

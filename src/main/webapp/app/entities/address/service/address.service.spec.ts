import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddress, Address } from '../address.model';

import { AddressService } from './address.service';

describe('Address Service', () => {
  let service: AddressService;
  let httpMock: HttpTestingController;
  let elemDefault: IAddress;
  let expectedResult: IAddress | IAddress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AddressService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      city: 'AAAAAAA',
      district: 'AAAAAAA',
      details: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Address()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          city: 'BBBBBB',
          district: 'BBBBBB',
          details: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Address', () => {
      const patchObject = Object.assign({}, new Address());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          city: 'BBBBBB',
          district: 'BBBBBB',
          details: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Address', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAddressToCollectionIfMissing', () => {
      it('should add a Address to an empty array', () => {
        const address: IAddress = { id: 123 };
        expectedResult = service.addAddressToCollectionIfMissing([], address);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(address);
      });

      it('should not add a Address to an array that contains it', () => {
        const address: IAddress = { id: 123 };
        const addressCollection: IAddress[] = [
          {
            ...address,
          },
          { id: 456 },
        ];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, address);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Address to an array that doesn't contain it", () => {
        const address: IAddress = { id: 123 };
        const addressCollection: IAddress[] = [{ id: 456 }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, address);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(address);
      });

      it('should add only unique Address to an array', () => {
        const addressArray: IAddress[] = [{ id: 123 }, { id: 456 }, { id: 23182 }];
        const addressCollection: IAddress[] = [{ id: 123 }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, ...addressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const address: IAddress = { id: 123 };
        const address2: IAddress = { id: 456 };
        expectedResult = service.addAddressToCollectionIfMissing([], address, address2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(address);
        expect(expectedResult).toContain(address2);
      });

      it('should accept null and undefined values', () => {
        const address: IAddress = { id: 123 };
        expectedResult = service.addAddressToCollectionIfMissing([], null, address, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(address);
      });

      it('should return initial array if no Address is added', () => {
        const addressCollection: IAddress[] = [{ id: 123 }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, undefined, null);
        expect(expectedResult).toEqual(addressCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

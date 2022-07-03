import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBasketItem, BasketItem } from '../basket-item.model';

import { BasketItemService } from './basket-item.service';

describe('BasketItem Service', () => {
  let service: BasketItemService;
  let httpMock: HttpTestingController;
  let elemDefault: IBasketItem;
  let expectedResult: IBasketItem | IBasketItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BasketItemService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      quantity: 0,
      totalCost: 0,
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

    it('should create a BasketItem', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new BasketItem()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BasketItem', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          quantity: 1,
          totalCost: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BasketItem', () => {
      const patchObject = Object.assign(
        {
          quantity: 1,
        },
        new BasketItem()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BasketItem', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          quantity: 1,
          totalCost: 1,
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

    it('should delete a BasketItem', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBasketItemToCollectionIfMissing', () => {
      it('should add a BasketItem to an empty array', () => {
        const basketItem: IBasketItem = { id: 123 };
        expectedResult = service.addBasketItemToCollectionIfMissing([], basketItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(basketItem);
      });

      it('should not add a BasketItem to an array that contains it', () => {
        const basketItem: IBasketItem = { id: 123 };
        const basketItemCollection: IBasketItem[] = [
          {
            ...basketItem,
          },
          { id: 456 },
        ];
        expectedResult = service.addBasketItemToCollectionIfMissing(basketItemCollection, basketItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BasketItem to an array that doesn't contain it", () => {
        const basketItem: IBasketItem = { id: 123 };
        const basketItemCollection: IBasketItem[] = [{ id: 456 }];
        expectedResult = service.addBasketItemToCollectionIfMissing(basketItemCollection, basketItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(basketItem);
      });

      it('should add only unique BasketItem to an array', () => {
        const basketItemArray: IBasketItem[] = [{ id: 123 }, { id: 456 }, { id: 50816 }];
        const basketItemCollection: IBasketItem[] = [{ id: 123 }];
        expectedResult = service.addBasketItemToCollectionIfMissing(basketItemCollection, ...basketItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const basketItem: IBasketItem = { id: 123 };
        const basketItem2: IBasketItem = { id: 456 };
        expectedResult = service.addBasketItemToCollectionIfMissing([], basketItem, basketItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(basketItem);
        expect(expectedResult).toContain(basketItem2);
      });

      it('should accept null and undefined values', () => {
        const basketItem: IBasketItem = { id: 123 };
        expectedResult = service.addBasketItemToCollectionIfMissing([], null, basketItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(basketItem);
      });

      it('should return initial array if no BasketItem is added', () => {
        const basketItemCollection: IBasketItem[] = [{ id: 123 }];
        expectedResult = service.addBasketItemToCollectionIfMissing(basketItemCollection, undefined, null);
        expect(expectedResult).toEqual(basketItemCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
